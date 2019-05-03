package inf112.skeleton.app;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Each client gets their own dedicated thread on the server.
 *
 * TODO: On disconnect the thread should attempt re-establish the connection.
 */
class ClientHandler extends Thread {
    private GameSocket gsock;
    private ArrayList<Card> selected_cards = null;
    private boolean cards_are_final = false;
    private GameServer gserv;
    private GameState state = GameState.CHOOSING_CARDS;
    private JSONObject round_cmd = null;

    public ClientHandler(GameServer gserv, GameSocket gsock) {
        this.gsock = gsock;
        this.gserv = gserv;
    }

    /**
     * Dumb polling loop to handle clients.
     */
    public void run() {
        final int WAIT_TIME = 64;

        for (;;) {
            try {
                synchronized (this) {
                    switch (state) {
                        case CHOOSING_CARDS: {
                            if (cards_are_final) break;
                            JSONObject rq = new JSONObject();
                            rq.put("cmd", "get_cards");
                            gsock.send(rq);
                            JSONObject ret = gsock.recv();
                            JSONSpecs.cards_order.check(ret);

                            selected_cards = Card.fromJSON(ret.getJSONArray("cards"));
                            cards_are_final = ret.getBoolean("final");
                        } break;

                        case STARTING_ROUND:
                            gsock.send(round_cmd);
                            state = GameState.RUNNING_ROUND;
                        break;

                        case RUNNING_ROUND: {
                            JSONObject ret = gsock.recv();
                            System.out.println(ret);
                            state = GameState.CHOOSING_CARDS;
                        } break;

                        default:
                    }
                }
            } catch (IOException e) {
                // TODO: FIXME: Handle reconnect.
                //System.out.println("Possibly lost connection to client: " + e);
            } catch (NoSuchResource e) {
                SystemPanic.panic("Not able to retrieve card textures.");
            }

            // Wait a number of milliseconds between requests,
            // this is dumb, but works.
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                ;
            }
        }
    }

    public ArrayList<Card> getCardSelection() {
        if (!cards_are_final)
            return null;
        return selected_cards;
    }

    public void runRound(JSONObject round_cmd) {
        synchronized (this) {
            cards_are_final = false;
            selected_cards = null;
            state = GameState.STARTING_ROUND;
            this.round_cmd = round_cmd;
        }
    }
}

/**
 * GameServer
 *
 * The host runs the GameServer as a separate thread and connects
 * to localhost.
 */
public class GameServer extends Thread {
    public final static String host = "0.0.0.0";
    public final static int port = 1337;
    private String init_key;
    private ServerSocket ssock;
    private Map<String, GameSocket> clients;
    private JSONObject game_settings;
    private Round round = null;
    private CardDeck deck;
    private GameState state = GameState.GAME_START;

    private GameSocket[] cons;
    private ClientHandler[] client_handlers;
    private int idx_count = 0;
    private int num_players;

    public GameServer(int num_players, JSONObject game_settings, String init_key) throws IOException, CSV.CSVError, NoSuchResource {
        ssock = new ServerSocket(port);
        clients = new HashMap<>();
        this.game_settings = game_settings;
        this.init_key = init_key;
        deck = new CardDeck(StaticConfig.GAME_CARDS_SRC);

        this.num_players = num_players;
        cons = new GameSocket[num_players];
        client_handlers = new ClientHandler[num_players];
        this.init_key = init_key;
    }

    // TODO: FIXME: Handle reconnects.
    public void listen() {
        System.out.println("Listening for connections ...");
        while (idx_count < num_players) {
            try {
                System.out.println("Waiting for conneciton ...");
                Socket con = ssock.accept();
                System.out.println("Got connection!");

                GameSocket gsock = new GameSocket(con, init_key);

                JSONObject game_init_cfg = new JSONObject();
                game_init_cfg.put("idx", idx_count);
                game_init_cfg.put("robots_pos", game_settings.get("robots_pos"));
                ArrayList<Card> cards = deck.get(9);
                JSONArray cards_jarr = new JSONArray();
                for (Card c : cards)
                    cards_jarr.put(c.asJSON());
                game_init_cfg.put("cards", cards_jarr);
                game_init_cfg.put("flags_pos", game_settings.get("flags_pos"));
                gsock.send(game_init_cfg);

                cons[idx_count] = gsock;
                client_handlers[idx_count] = new ClientHandler(this, gsock);
                client_handlers[idx_count].start();

                idx_count++;
            } catch (DecryptionException e) {
                System.out.println("WARNING: Connected with wrong key");
            } catch (GameSocketException e) {
                System.out.println("WARNING: Possible version mismatch between server and client.");
            } catch (IOException e) {
                System.out.println("ERROR: Unable to accept socket connection: " + e);
            }  catch (CardDeck.NoMoreCards e) {
                System.out.println("ERROR: Unable to retrieve cards");
            }

            System.out.println("Finished waiting for all players.");
        }
    }

    /**
     * Run the game.
     *
     * This is implemented as a dumb polling loop.
     */
    public void run() {
        final long WAIT_TIME = 64;

        // Wait for clients to connect.
        listen();

        for (;;) {
            sw: switch (state) {
                case GAME_START:
                    state = GameState.CHOOSING_CARDS;
                break;

                case CHOOSING_CARDS:
                    for (ClientHandler handler : client_handlers)
                        if (handler.getCardSelection() == null)
                            break sw;

                    JSONArray pl_cards = new JSONArray();
                    for (ClientHandler handler : client_handlers) {
                        JSONArray cards = new JSONArray();
                        for (Card c : handler.getCardSelection())
                            cards.put(c.asJSON());
                        pl_cards.put(cards);
                    }

                    JSONObject round_obj = new JSONObject();
                    round_obj.put("cmd", "run_round");
                    round_obj.put("player_cards", pl_cards);

                    for (ClientHandler handler : client_handlers)
                        handler.runRound(round_obj);
                break;
            }

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException | IllegalMonitorStateException e) {
                ;
            }
        }
    }

    public static String generateKey() {
        final int NUM_CHARS = 3;
        final int NUM_DIGITS = 3;
        char alpha[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char digits[] = "0123456789".toCharArray();
        SecureRandom sr = new SecureRandom();
        String key = "";
        for (int i = 0; i < NUM_CHARS; i++)
            key += alpha[sr.nextInt(alpha.length)];
        for (int i = 0; i < NUM_DIGITS; i++)
            key += digits[sr.nextInt(digits.length)];
        return key;
    }
}
