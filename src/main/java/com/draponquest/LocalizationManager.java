package com.draponquest;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages multi-language support for DraponQuest
 * Supports English and Japanese text
 */
public class LocalizationManager {
    
    // Language constants
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_JAPANESE = "ja";
    
    // Current language
    private static String currentLanguage = LANG_ENGLISH;
    
    // Text data for different languages
    private static final Map<String, Map<String, String>> textData = new HashMap<>();
    
    static {
        initializeTextData();
    }
    
    /**
     * Initialize all text data for both languages
     */
    private static void initializeTextData() {
        // English text
        Map<String, String> englishText = new HashMap<>();
        
        // Game instructions
        englishText.put("welcome", "Welcome to DraponQuest!");
        englishText.put("explore", "Explore the world using the arrow keys.");
        englishText.put("command_menu", "Open the command menu with ENTER.");
        englishText.put("score_info", "Each time you move, your score increases by 1.");
        englishText.put("goal", "Try to get the highest score by surviving and exploring!");
        englishText.put("battle_info", "Fight monsters, defend to reduce damage, or run from tough battles.");
        englishText.put("defeat_info", "If you are defeated, your total score will be shown.");
        englishText.put("save_info", "Save with F5, load with F9. Good luck, hero!");
        
        // Menu items
        englishText.put("menu_talk", "TALK");
        englishText.put("menu_check", "CHECK");
        englishText.put("menu_magic", "MAGIC");
        englishText.put("menu_item", "ITEM");
        englishText.put("menu_status", "STATUS");
        
        // Battle text
        englishText.put("battle_title", "BATTLE! (ESC to exit)");
        englishText.put("battle_attack", "A: Attack");
        englishText.put("battle_defend", "D: Defend");
        englishText.put("battle_run", "R: Run");
        englishText.put("battle_actions", "A: Attack   D: Defend   R: Run");
        englishText.put("player_hp", "Player HP: ");
        englishText.put("monster_hp", " HP: ");
        englishText.put("no_monster_image", "No monster image");
        
        // Battle messages
        englishText.put("battle_you_deal", "You deal ");
        englishText.put("battle_damage", " damage!");
        englishText.put("battle_you_defend", "You defend!");
        englishText.put("battle_escaped", "You escaped successfully!");
        englishText.put("battle_escape_failed", "Escape failed!");
        englishText.put("battle_monster_deals", " deals ");
        englishText.put("battle_you_won", "You won the battle!");
        englishText.put("battle_gained", "You gained ");
        englishText.put("battle_you_defeated", "You were defeated!");
        englishText.put("battle_ongoing", "Battle ongoing, not exiting");
        
        // Event text
        englishText.put("event_title", "EVENT! (ESC to exit)");
        
        // Game over text
        englishText.put("game_over", "GAME OVER");
        englishText.put("press_enter_restart", "Press ENTER to restart");
        englishText.put("total_score", "Total Score: ");
        englishText.put("battles_won", "Battles Won: ");
        
        // Save/Load messages
        englishText.put("save_success", "Game saved successfully!");
        englishText.put("save_failed", "Save failed: ");
        englishText.put("load_success", "Game loaded successfully!");
        englishText.put("load_failed", "Load failed: ");
        
        // Command messages
        englishText.put("command_selected", "You selected ");
        
        // Audio status
        englishText.put("audio_music", "Music: ");
        englishText.put("audio_sound", "Sound: ");
        englishText.put("audio_on", "ON");
        englishText.put("audio_off", "OFF");
        englishText.put("audio_vol", "Vol: ");
        
        textData.put(LANG_ENGLISH, englishText);
        
        // Japanese text
        Map<String, String> japaneseText = new HashMap<>();
        
        // Game instructions
        japaneseText.put("welcome", "ドラポンクエストへようこそ！");
        japaneseText.put("explore", "矢印キーで世界を探索してください。");
        japaneseText.put("command_menu", "ENTERキーでコマンドメニューを開きます。");
        japaneseText.put("score_info", "移動するたびにスコアが1増加します。");
        japaneseText.put("goal", "生き残って探索して最高スコアを目指してください！");
        japaneseText.put("battle_info", "モンスターと戦い、防御でダメージを減らすか、強敵から逃げてください。");
        japaneseText.put("defeat_info", "倒された場合、総スコアが表示されます。");
        japaneseText.put("save_info", "F5でセーブ、F9でロード。頑張ってください、勇者！");
        
        // Menu items
        japaneseText.put("menu_talk", "はなす");
        japaneseText.put("menu_check", "しらべる");
        japaneseText.put("menu_magic", "まほう");
        japaneseText.put("menu_item", "どうぐ");
        japaneseText.put("menu_status", "じょうたい");
        
        // Battle text
        japaneseText.put("battle_title", "たたかい！(ESCで終了)");
        japaneseText.put("battle_attack", "A: こうげき");
        japaneseText.put("battle_defend", "D: ぼうぎょ");
        japaneseText.put("battle_run", "R: にげる");
        japaneseText.put("battle_actions", "A: こうげき   D: ぼうぎょ   R: にげる");
        japaneseText.put("player_hp", "プレイヤーHP: ");
        japaneseText.put("monster_hp", " HP: ");
        japaneseText.put("no_monster_image", "モンスター画像なし");
        
        // Battle messages
        japaneseText.put("battle_you_deal", "あなたは ");
        japaneseText.put("battle_damage", " ダメージを与えた！");
        japaneseText.put("battle_you_defend", "あなたは防御した！");
        japaneseText.put("battle_escaped", "逃げ出した！");
        japaneseText.put("battle_escape_failed", "逃げ出しに失敗した！");
        japaneseText.put("battle_monster_deals", " は ");
        japaneseText.put("battle_you_won", "戦いに勝利した！");
        japaneseText.put("battle_gained", "経験値とゴールドを獲得しました！");
        japaneseText.put("battle_you_defeated", "あなたは倒された！");
        japaneseText.put("battle_ongoing", "戦闘中、終了しません");
        
        // Event text
        japaneseText.put("event_title", "イベント！(ESCで終了)");
        
        // Game over text
        japaneseText.put("game_over", "ゲームオーバー");
        japaneseText.put("press_enter_restart", "ENTERキーでリスタート");
        japaneseText.put("total_score", "総スコア: ");
        japaneseText.put("battles_won", "勝利した戦闘: ");
        
        // Save/Load messages
        japaneseText.put("save_success", "ゲームをセーブしました！");
        japaneseText.put("save_failed", "セーブに失敗: ");
        japaneseText.put("load_success", "ゲームをロードしました！");
        japaneseText.put("load_failed", "ロードに失敗: ");
        
        // Command messages
        japaneseText.put("command_selected", "選択したコマンド: ");
        
        // Audio status
        japaneseText.put("audio_music", "音楽: ");
        japaneseText.put("audio_sound", "効果音: ");
        japaneseText.put("audio_on", "オン");
        japaneseText.put("audio_off", "オフ");
        japaneseText.put("audio_vol", "音量: ");
        
        textData.put(LANG_JAPANESE, japaneseText);
    }
    
    /**
     * Get text for the current language
     * @param key The text key
     * @return The localized text
     */
    public static String getText(String key) {
        Map<String, String> currentText = textData.get(currentLanguage);
        if (currentText != null && currentText.containsKey(key)) {
            return currentText.get(key);
        }
        // Fallback to English if not found
        Map<String, String> englishText = textData.get(LANG_ENGLISH);
        return englishText.getOrDefault(key, key);
    }
    
    /**
     * Set the current language
     * @param language The language code (LANG_ENGLISH or LANG_JAPANESE)
     */
    public static void setLanguage(String language) {
        if (LANG_ENGLISH.equals(language) || LANG_JAPANESE.equals(language)) {
            currentLanguage = language;
        }
    }
    
    /**
     * Get the current language
     * @return The current language code
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Toggle between English and Japanese
     */
    public static void toggleLanguage() {
        if (LANG_ENGLISH.equals(currentLanguage)) {
            currentLanguage = LANG_JAPANESE;
        } else {
            currentLanguage = LANG_ENGLISH;
        }
    }
    
    /**
     * Get the display name for the current language
     * @return The language display name
     */
    public static String getLanguageDisplayName() {
        return LANG_ENGLISH.equals(currentLanguage) ? "English" : "日本語";
    }
} 