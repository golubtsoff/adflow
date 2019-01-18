package entity.campaign;

public enum Status {
    removed("removed"),
    auto("auto"),
    checking("checking"),
    rejected("rejected"),
    frozen("frozen");

    Status(String value) {
    }
}
