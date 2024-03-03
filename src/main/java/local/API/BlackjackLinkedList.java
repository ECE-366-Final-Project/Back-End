package local.API;

public class BlackjackLinkedList {

    private BlackjackGame last;
    private BlackjackGame first;

    public BlackjackLinkedList() {
        last = null;
        first = null;
    }

    public void add(BlackjackGame game) {
        if (first == null) {
            last = game;
            first = game;
            game.next = null;
            game.prev = null;
            return;
        }
        last.next = game;
        game.prev = last;
        last = game;
        game.next = null;
    }

    public void remove(BlackjackGame game) {
        if (game.prev != null) {
            game.prev.next = game.next;
        }
        if (game.next != null) {
            game.next.prev = game.prev;
        }
        return;
    }
}