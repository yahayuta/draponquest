package com.draponquest;

import javafx.scene.input.KeyCode;
import com.draponquest.Monster;
import javafx.application.Platform;

public class BattleManager {

    private DraponQuestFX game;
    private Monster currentMonster;
    private int monsterHP;
    private boolean playerTurn;
    private boolean isDefending;
    private String battleMessage;

    public BattleManager(DraponQuestFX game) {
        this.game = game;
    }

    public void startBattle() {
        System.out.println("Battle started. playerHP=" + game.playerHP);
        game.currentMode = DraponQuestFX.MODE_BATTLE;
        // Randomly select a monster
        currentMonster = game.monsters[(int) (Math.random() * game.monsters.length)];
        System.out.println("Selected monster: " + currentMonster.name + " (HP: " + currentMonster.maxHP + ", Attack: "
                + currentMonster.attack + ", Defense: " + currentMonster.defense + ")");
        monsterHP = currentMonster.maxHP;
        playerTurn = true;
        isDefending = false; // Reset defending state
        battleMessage = "";

        // NES-style message: Monster appears
        game.displayMessage(currentMonster.name + LocalizationManager.getText("battle_appears") + "E");

        game.setPreBattleMusic(game.audioManager.getCurrentMusicTrack());
        // Play battle start sound and music
        game.audioManager.playSound(AudioManager.SOUND_BATTLE_START);
        game.audioManager.playMusic(AudioManager.MUSIC_BATTLE);
    }

    public void handleBattleInput(KeyCode keyCode) {
        System.out.println("Battle input: " + keyCode + ", playerTurn=" + playerTurn + ", playerHP=" + game.playerHP
                + ", monsterHP=" + monsterHP);
        if (game.playerHP <= 0 || monsterHP <= 0) {
            System.out.println("Battle input ignored: battle is over");
            return; // Battle is over
        }

        if (playerTurn) {
            switch (keyCode) {
                case A:
                    int damage = Math.max(1, game.playerAttack - currentMonster.defense);
                    monsterHP -= damage;
                    System.out.println("Player attacks: monsterHP=" + monsterHP);
                    game.audioManager.playSound(AudioManager.SOUND_ATTACK);

                    String attackMsg = LocalizationManager.getText("battle_you_deal") + damage
                            + LocalizationManager.getText("battle_damage") + "E";

                    game.displayMessage(attackMsg, () -> {
                        if (monsterHP <= 0) {
                            checkVictory();
                        } else {
                            monsterTurn();
                        }
                    });
                    playerTurn = false;
                    break;

                case D:
                    System.out.println("Player defends");
                    game.audioManager.playSound(AudioManager.SOUND_DEFEND);
                    isDefending = true;

                    game.displayMessage(LocalizationManager.getText("battle_you_defend") + "E", () -> {
                        monsterTurn();
                    });
                    playerTurn = false;
                    break;

                case R:
                    // Try to escape: 50% chance
                    if (Math.random() < 0.5) {
                        System.out.println("Player escaped from battle");
                        game.displayMessage(LocalizationManager.getText("battle_escaped") + "E", () -> {
                            game.currentMode = DraponQuestFX.MODE_MOVE;
                            game.audioManager.playSound(AudioManager.SOUND_ESCAPE);
                            game.audioManager.playMusic(game.getPreBattleMusic());
                        });
                        return;
                    } else {
                        System.out.println("Player failed to escape");
                        game.audioManager.playSound(AudioManager.SOUND_DEFEAT);
                        game.displayMessage(LocalizationManager.getText("battle_escape_failed") + "E", () -> {
                            monsterTurn();
                        });
                        playerTurn = false;
                    }
                    break;
            }
        }
    }

    private void monsterTurn() {
        if (monsterHP <= 0)
            return;

        int monsterDamage = Math.max(1, currentMonster.attack - game.playerDefense);
        if (isDefending) {
            monsterDamage = (int) (monsterDamage * 0.5); // Reduce damage by 50% if defending
            isDefending = false;
        }
        game.playerHP -= monsterDamage;
        System.out.println("Monster attacks: playerHP=" + game.playerHP);

        String monsterMsg = currentMonster.name + LocalizationManager.getText("battle_monster_deals")
                + monsterDamage + LocalizationManager.getText("battle_damage") + "E";

        game.displayMessage(monsterMsg, () -> {
            if (game.playerHP <= 0) {
                checkDefeat();
            } else {
                playerTurn = true;
            }
        });
    }

    private void checkVictory() {
        monsterHP = 0;
        game.battlesWon++;
        game.playerXP += currentMonster.xpValue;
        game.playerGold += currentMonster.goldValue;

        // --- Item Drop Logic ---
        String itemDropMessagePart = "";
        if (currentMonster.itemDrop != null && Math.random() < currentMonster.dropChance) {
            game.getInventory().addItem(currentMonster.itemDrop);
            itemDropMessagePart = "@" + currentMonster.name + " dropped a " + currentMonster.itemDrop.getName() + "!";
        }

        // NES-style victory message
        String winMsg = currentMonster.name + " is defeated!@" +
                LocalizationManager.getText("battle_gained") + " " + currentMonster.xpValue + " XP\n" +
                "and " + currentMonster.goldValue + " gold!" + itemDropMessagePart + "E";

        game.displayMessage(winMsg, () -> {
            if (game.playerXP >= game.xpToNextLevel) {
                game.levelUp();
            }
            game.audioManager.playMusic(game.getPreBattleMusic());
            game.currentMode = DraponQuestFX.MODE_MOVE;
        });

        System.out.println("Monster defeated. Player wins. Total battles won: " + game.battlesWon);

        // Play victory sound and music
        game.audioManager.playSound(AudioManager.SOUND_VICTORY);
        game.audioManager.playMusic(AudioManager.MUSIC_VICTORY);
    }

    private void checkDefeat() {
        game.playerHP = 0;
        game.displayMessage("You were defeated!E");
        game.currentGameStatus = 4; // GAME_OVER
        System.out.println("Player defeated. GAME_OVER");
        // Play defeat sound and game over music
        game.audioManager.playSound(AudioManager.SOUND_DEFEAT);
        game.audioManager.playSound(AudioManager.SOUND_GAME_OVER);
    }

    public String getBattleMessage() {
        return battleMessage;
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }

    public int getMonsterHP() {
        return monsterHP;
    }
}
