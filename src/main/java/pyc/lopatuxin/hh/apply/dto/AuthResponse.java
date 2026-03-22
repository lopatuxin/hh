package pyc.lopatuxin.hh.apply.dto;

public record AuthResponse(String status) {

    public static AuthResponse opened() {
        return new AuthResponse("OPENED");
    }

    public static AuthResponse saved() {
        return new AuthResponse("SAVED");
    }

    public static AuthResponse cancelled() {
        return new AuthResponse("CANCELLED");
    }
}
