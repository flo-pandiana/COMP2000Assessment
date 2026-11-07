public class HeatDecorators {
    public static class Base implements HeatCalculator {
        private static final float BASE_HEAT = 15f;

        @Override
        public float calculate(int fireIntensity) {
            return BASE_HEAT;
        }
    }

    public abstract static class Layer implements HeatCalculator {
        protected HeatCalculator wrapped;

        protected Layer(HeatCalculator wrapped) {
            if (wrapped == null) {
                throw new IllegalArgumentException("Wrapped calculator cannot be null");
            }

            this.wrapped = wrapped;
        }
    }

    public static class Moisture extends Layer {
        private Vegetation vegetation;

        public Moisture(HeatCalculator wrapped, Vegetation vegetation) {
            super(wrapped);
            this.vegetation = vegetation;
        }

        @Override
        public float calculate(int fireIntensity) {
            float moistureFactor = 1f - (vegetation.getMoisture() * 0.5f);

            return wrapped.calculate(fireIntensity) * moistureFactor;
        }
    }

    public static class Age extends Layer {
        private Vegetation vegetation;

        public Age(HeatCalculator wrapped, Vegetation vegetation) {
            super(wrapped);
            this.vegetation = vegetation;
        }

        @Override
        public float calculate(int fireIntensity) {
            float ageFactor = Math.max(0.3f, 1.3f - (vegetation.getAge() * 0.06f));

            return wrapped.calculate(fireIntensity) * ageFactor;
        }
    }

    public static class Intensity extends Layer {
        public Intensity(HeatCalculator wrapped) {
            super(wrapped);
        }

        @Override
        public float calculate(int fireIntensity) {
            float intensityFactor = 0.5f + (fireIntensity / 20f);

            return wrapped.calculate(fireIntensity) * intensityFactor;
        }
    }

    public static class HeightBonus extends Layer {
        private Tree tree;

        public HeightBonus(HeatCalculator wrapped, Tree tree) {
            super(wrapped);
            this.tree = tree;
        }

        @Override
        public float calculate(int fireIntensity) {
            return wrapped.calculate(fireIntensity) + tree.getHeight();
        }
    }

    public static class DensityBonus extends Layer {
        private Grass grass;

        public DensityBonus(HeatCalculator wrapped, Grass grass) {
            super(wrapped);
            this.grass = grass;
        }

        @Override
        public float calculate(int fireIntensity) {
            return wrapped.calculate(fireIntensity) + grass.getDensity();
        }
    }
}
