package App.controller;

public class Model {

        private long id;
        private String status, timestamp;

        public Model(long id, String status, String timestamp) {
            this.id = id;
            this.status = status;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format(
                    "Model[id=%d, status='%s', timestamp='%s']",
                    id, status, timestamp);
        }

        // getters & setters опущены для краткости

}
