package com.darkxell.common.util.language;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a message to be translated. Contains an ID of message, and may contain replacements to make after
 * translating.
 */
public class Message {

    /** ID of the message. */
    public final String id;
    /** Keyword values contained in this Message. */
    private ArrayList<String> keywords = new ArrayList<>();
    /**
     * ID of the last language this message was translated with. Used to update the translation when the user changes
     * the Language.
     */
    private String lastLang = null;
    private ArrayList<Message> prefixes = new ArrayList<>(), suffixes = new ArrayList<>();
    private HashMap<String, Message> replacements = new HashMap<>();
    /** False if this Message's ID shouldn't be translated. */
    public final boolean shouldTranslate;
    /** Translated value of this Message. Used to avoid translating every time this Message is used. */
    private String value = "";

    public Message(String id) {
        this(id, true);
    }

    public Message(String id, boolean shouldTranslate) {
        this.id = id;
        this.shouldTranslate = shouldTranslate;
    }

    /** Adds some text at the beginning of this Message, after translation. */
    public Message addPrefix(Message prefix) {
        this.prefixes.add(prefix);
        return this;
    }

    /** Adds some text at the beginning of this Message, after translation. */
    public Message addPrefix(String prefix) {
        this.prefixes.add(new Message(prefix, false));
        return this;
    }

    /**
     * Adds a replacement to this Message.
     *
     * @param pattern - The pattern to replace.
     * @param message - The message to replace with.
     */
    public Message addReplacement(String pattern, Message message) {
        this.replacements.put(pattern, message);
        return this;
    }

    /**
     * Adds a replacement to this Message.
     *
     * @param pattern - The pattern to replace.
     * @param message - The message to replace with.
     */
    public Message addReplacement(String pattern, String message) {
        return this.addReplacement(pattern, new Message(message, false));
    }

    /** Adds some text at the end of this Message, after translation. */
    public Message addSuffix(Message suffix) {
        this.suffixes.add(suffix);
        return this;
    }

    /** Adds some text at the end of this Message, after translation. */
    public Message addSuffix(String suffix) {
        this.suffixes.add(new Message(suffix, false));
        return this;
    }

    /** @return This message as a String, without <> content. */
    public String asText() {
        String text = this.toString(), out = "";
        int tagCount = 0;
        for (char c : text.toCharArray())
            if (c == '<')
                ++tagCount;
            else if (c == '>')
                --tagCount;
            else if (tagCount == 0)
                out += c;
        return out;
    }

    private void colorKeywords() {
        for (String keyword : this.keywords) {
            int index = this.value.toLowerCase().indexOf(keyword.toLowerCase());
            this.value = this.value.substring(0, index) + "<green>"
                    + this.value.substring(index, index + keyword.length()) + "</color>"
                    + this.value.substring(index + keyword.length());
        }
    }

    private void executeSubtranslations() {
        while (this.value.contains("<translate>")) {
            int pos = this.value.indexOf("<translate>");
            this.value = StringUtil.remove(this.value, pos, "<translate>".length());

            if (this.value.contains("</translate>")) {
                int endpos = this.value.indexOf("</translate>");
                this.value = StringUtil.remove(this.value, endpos, "</translate>".length());

                if (pos != endpos) {
                    String idToTranslate = this.value.subSequence(pos, endpos).toString();
                    this.value = StringUtil.remove(this.value, pos, idToTranslate.length());

                    String translated = Localization.translate(idToTranslate);
                    if (!translated.equals(idToTranslate))
                        this.value = StringUtil.insert(this.value, translated, pos);
                }
            }
        }
    }

    public Message findKeywords() {
        this.keywords.clear();
        this.keywords.addAll(Keywords.findKeywords(this.toString()));

        this.colorKeywords();
        return this;
    }

    public String[] getKeywords() {
        return this.keywords.toArray(new String[0]);
    }

    /** @return The translated value of this Message. */
    @Override
    public String toString() {
        if (!Localization.getLanguage().id.equals(this.lastLang))
            this.update();
        return this.value;
    }

    private void update() {
        this.value = this.shouldTranslate ? Localization.translate(this.id) : this.id;

        this.executeSubtranslations();

        for (String pattern : this.replacements.keySet())
            this.value = this.value.replaceAll(pattern, this.replacements.get(pattern).toString());

        for (Message prefix : this.prefixes)
            this.value = prefix.toString() + this.value;

        for (Message suffix : this.suffixes)
            this.value += suffix.toString();

        this.lastLang = Localization.getLanguage().id;

        this.colorKeywords();
    }

}
