package sample.client.model;

import sample.client.model.MusicPlayerState;

public interface OnStateChangedEvent {
    void OnStateChanged(MusicPlayerState currentState);
}
