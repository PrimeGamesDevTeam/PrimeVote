package net.primegames.data;

public enum ClaimStatus {
    CLAIMED,
    AVAILABLE,
    NOT_VOTED,
    UNKNOWN;

    public static ClaimStatus getStatus(int i) {
        return switch (i) {
            case 1 -> AVAILABLE;
            case 2 -> CLAIMED;
            default -> NOT_VOTED;
        };
    }
}
