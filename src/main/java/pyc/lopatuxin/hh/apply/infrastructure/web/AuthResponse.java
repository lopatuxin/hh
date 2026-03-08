package pyc.lopatuxin.hh.apply.infrastructure.web;

public record AuthResponse(String status, String path) {

    public static AuthResponse opened() {
        return new AuthResponse("OPENED", null);
    }

    public static AuthResponse saved(String path) {
        return new AuthResponse("SAVED", path);
    }
}