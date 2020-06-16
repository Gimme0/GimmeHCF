package me.gimme.gimmehcf.player;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerStats {

    private int kills = 0;
    private int deaths = 0;

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public double getKD() {
        return ((double) kills) / (deaths == 0 ? 1 : deaths);
    }

}
