    package inf112.skeleton.app;

    public enum DamageType {
        /**
         * Describes which damage type the robot recieves and the amount of health tokens it recieves based on damage type
         */

            LASER,
            FALL;

            public static int getDamage(inf112.skeleton.app.DamageType d) {
                switch (d) {
                    case FALL:
                        return 10;
                    case LASER:
                        return 1;
                }
                throw new UnsupportedOperationException();
            }
        }

