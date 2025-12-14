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
        currentMonster = game.monsters[(int)(Math.random() * game.monsters.length)];
        System.out.println("Selected monster: " + currentMonster.name + " (HP: " + currentMonster.maxHP + ", Attack: " + currentMonster.attack + ", Defense: " + currentMonster.defense + ")");
        monsterHP = currentMonster.maxHP;
        playerTurn = true;
        isDefending = false; // Reset defending state
        battleMessage = "";
        
        // Play battle start sound and music
        game.audioManager.playSound(AudioManager.SOUND_BATTLE_START);
        game.audioManager.playMusic(AudioManager.MUSIC_BATTLE);
    }

    public void handleBattleInput(KeyCode keyCode) {
        System.out.println("Battle input: " + keyCode + ", playerTurn=" + playerTurn + ", playerHP=" + game.playerHP + ", monsterHP=" + monsterHP);
        if (game.playerHP <= 0 || monsterHP <= 0) {
            System.out.println("Battle input ignored: battle is over");
            return; // Battle is over
        }
        
        if (playerTurn) {
            switch (keyCode) {
                case A:
                    int damage = Math.max(1, game.playerAttack - currentMonster.defense);
                    monsterHP -= damage;
                    battleMessage = LocalizationManager.getText("battle_you_deal") + damage + LocalizationManager.getText("battle_damage");
                    System.out.println("Player attacks: monsterHP=" + monsterHP);
                    playerTurn = false;
                    // Play attack sound
                    game.audioManager.playSound(AudioManager.SOUND_ATTACK);
                    break;
                case D:
                    battleMessage = LocalizationManager.getText("battle_you_defend");
                    System.out.println("Player defends");
                    playerTurn = false;
                    isDefending = true;
                    // Play defend sound
                    game.audioManager.playSound(AudioManager.SOUND_DEFEND);
                    break;
                case R:
                    // Try to escape: 50% chance
                    if (Math.random() < 0.5) {
                        battleMessage = LocalizationManager.getText("battle_escaped");
                        System.out.println("Player escaped from battle");
                        game.currentMode = DraponQuestFX.MODE_MOVE;
                        // Play escape sound and return to field music
                        game.audioManager.playSound(AudioManager.SOUND_ESCAPE);
                        game.audioManager.playMusic(AudioManager.MUSIC_FIELD);
                        return; // Exit battle immediately
                    } else {
                        battleMessage = LocalizationManager.getText("battle_escape_failed");
                        System.out.println("Player failed to escape");
                        playerTurn = false;
                        // Play escape failed sound
                        game.audioManager.playSound(AudioManager.SOUND_DEFEAT);
                    }
                    break;
            }
        }
        
        // Monster's turn
        if (!playerTurn) {
            int monsterDamage = Math.max(1, currentMonster.attack - game.playerDefense);
            if (isDefending) {
                monsterDamage = (int)(monsterDamage * 0.5); // Reduce damage by 50% if defending
                isDefending = false;
            }
            game.playerHP -= monsterDamage;
            battleMessage = currentMonster.name + LocalizationManager.getText("battle_monster_deals") + monsterDamage + LocalizationManager.getText("battle_damage");
            System.out.println("Monster attacks: playerHP=" + game.playerHP);
            playerTurn = true;
            
            // Check for game over after monster attack
            if (game.playerHP <= 0) {
                game.playerHP = 0;
                battleMessage = "You were defeated!";
                game.currentGameStatus = 4; // GAME_OVER
                System.out.println("Player defeated. GAME_OVER");
                // Play defeat sound and game over music
                game.audioManager.playSound(AudioManager.SOUND_DEFEAT);
                game.audioManager.playSound(AudioManager.SOUND_GAME_OVER);
                return; // Exit battle immediately
            }
        }
        
        // Check for battle victory (only if player is still alive)
        if (game.playerHP > 0 && monsterHP <= 0) {
            monsterHP = 0;
            game.battlesWon++;
            game.playerXP += currentMonster.xpValue;
            game.playerGold += currentMonster.goldValue;
            
            // Set the new battle reward message in DraponQuestFX
            game.battleRewardMessage = LocalizationManager.getText("battle_you_won") + " " + LocalizationManager.getText("battle_gained") + " " + currentMonster.xpValue + " XP and " + currentMonster.goldValue + " gold!";
            game.battleRewardMessageTime = System.currentTimeMillis();

            System.out.println("Monster defeated. Player wins. Total battles won: " + game.battlesWon);
            
            // Play victory sound and music, then return to field music
            game.audioManager.playSound(AudioManager.SOUND_VICTORY);
            game.audioManager.playMusic(AudioManager.MUSIC_VICTORY);
            // Return to field music after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds (for music and message display)
                    javafx.application.Platform.runLater(() -> {
                        if (game.playerXP >= game.xpToNextLevel) {
                            game.levelUp();
                        }
                        game.audioManager.playMusic(AudioManager.MUSIC_FIELD);
                        game.currentMode = DraponQuestFX.MODE_MOVE; // Set mode to MOVE after delay
                    });
                } catch (InterruptedException e) {
                    // Ignore interruption
                }
            }).start();
        }
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
