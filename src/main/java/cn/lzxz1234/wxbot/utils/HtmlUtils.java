package cn.lzxz1234.wxbot.utils;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.util.IOUtils;

public class HtmlUtils {

    private static final HtmlCharacterEntityReferences characterEntityReferences =
            new HtmlCharacterEntityReferences();
    
    public static String htmlUnescape(String input) {
        if (input == null) {
            return null;
        }
        return new HtmlCharacterEntityDecoder(characterEntityReferences, input).decode();
    }
    
    private static class HtmlCharacterEntityDecoder {

        private static final int MAX_REFERENCE_SIZE = 10;
        private final HtmlCharacterEntityReferences characterEntityReferences;
        private final String originalMessage;
        private final StringBuilder decodedMessage;
        private int currentPosition = 0;
        private int nextPotentialReferencePosition = -1;
        private int nextSemicolonPosition = -2;


        public HtmlCharacterEntityDecoder(HtmlCharacterEntityReferences characterEntityReferences, String original) {
            this.characterEntityReferences = characterEntityReferences;
            this.originalMessage = original;
            this.decodedMessage = new StringBuilder(originalMessage.length());
        }

        public String decode() {
            while (currentPosition < originalMessage.length()) {
                findNextPotentialReference(currentPosition);
                copyCharactersTillPotentialReference();
                processPossibleReference();
            }
            return decodedMessage.toString();
        }

        private void findNextPotentialReference(int startPosition) {
            nextPotentialReferencePosition = Math.max(startPosition, nextSemicolonPosition - MAX_REFERENCE_SIZE);

            do {
                nextPotentialReferencePosition =
                        originalMessage.indexOf('&', nextPotentialReferencePosition);

                if (nextSemicolonPosition != -1 &&
                        nextSemicolonPosition < nextPotentialReferencePosition)
                    nextSemicolonPosition = originalMessage.indexOf(';', nextPotentialReferencePosition + 1);

                boolean isPotentialReference =
                        nextPotentialReferencePosition != -1
                        && nextSemicolonPosition != -1
                        && nextPotentialReferencePosition - nextSemicolonPosition < MAX_REFERENCE_SIZE;

                if (isPotentialReference) {
                    break;
                }
                if (nextPotentialReferencePosition == -1) {
                    break;
                }
                if (nextSemicolonPosition == -1) {
                    nextPotentialReferencePosition = -1;
                    break;
                }

                nextPotentialReferencePosition = nextPotentialReferencePosition + 1;
            }
            while (nextPotentialReferencePosition != -1);
        }


        private void copyCharactersTillPotentialReference() {
            if (nextPotentialReferencePosition != currentPosition) {
                int skipUntilIndex = nextPotentialReferencePosition != -1 ?
                        nextPotentialReferencePosition : originalMessage.length();
                if (skipUntilIndex - currentPosition > 3) {
                    decodedMessage.append(originalMessage.substring(currentPosition, skipUntilIndex));
                    currentPosition = skipUntilIndex;
                }
                else {
                    while (currentPosition < skipUntilIndex)
                        decodedMessage.append(originalMessage.charAt(currentPosition++));
                }
            }
        }

        private void processPossibleReference() {
            if (nextPotentialReferencePosition != -1) {
                boolean isNumberedReference = originalMessage.charAt(currentPosition + 1) == '#';
                boolean wasProcessable = isNumberedReference ? processNumberedReference() : processNamedReference();
                if (wasProcessable) {
                    currentPosition = nextSemicolonPosition + 1;
                }
                else {
                    char currentChar = originalMessage.charAt(currentPosition);
                    decodedMessage.append(currentChar);
                    currentPosition++;
                }
            }
        }

        private boolean processNumberedReference() {
            boolean isHexNumberedReference =
                    originalMessage.charAt(nextPotentialReferencePosition + 2) == 'x' ||
                    originalMessage.charAt(nextPotentialReferencePosition + 2) == 'X';
            try {
                int value = (!isHexNumberedReference) ?
                        Integer.parseInt(getReferenceSubstring(2)) :
                        Integer.parseInt(getReferenceSubstring(3), 16);
                decodedMessage.append((char) value);
                return true;
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }

        private boolean processNamedReference() {
            String referenceName = getReferenceSubstring(1);
            char mappedCharacter = characterEntityReferences.convertToCharacter(referenceName);
            if (mappedCharacter != HtmlCharacterEntityReferences.CHAR_NULL) {
                decodedMessage.append(mappedCharacter);
                return true;
            }
            return false;
        }

        private String getReferenceSubstring(int referenceOffset) {
            return originalMessage.substring(nextPotentialReferencePosition + referenceOffset, nextSemicolonPosition);
        }

    }
    private static class HtmlCharacterEntityReferences {

        static final char REFERENCE_START = '&';
        static final char REFERENCE_END = ';';
        static final char CHAR_NULL = (char) -1;
        private static final String PROPERTIES_FILE = "HtmlCharacterEntityReferences.properties";
        private final String[] characterToEntityReferenceMap = new String[3000];
        private final Map<String, Character> entityReferenceToCharacterMap = new HashMap<String, Character>(252);

        public HtmlCharacterEntityReferences() {
            Properties entityReferences = new Properties();
            InputStream is = HtmlCharacterEntityReferences.class.getResourceAsStream(PROPERTIES_FILE);
            try {
            entityReferences.load(is);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.close(is);
            }

            Enumeration<?> keys = entityReferences.propertyNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                int referredChar = Integer.parseInt(key);
                int index = (referredChar < 1000 ? referredChar : referredChar - 7000);
                String reference = entityReferences.getProperty(key);
                this.characterToEntityReferenceMap[index] = REFERENCE_START + reference + REFERENCE_END;
                this.entityReferenceToCharacterMap.put(reference, new Character((char) referredChar));
            }
        }

        public char convertToCharacter(String entityReference) {
            Character referredCharacter = (Character) this.entityReferenceToCharacterMap.get(entityReference);
            if (referredCharacter != null) {
                return referredCharacter.charValue();
            }
            return CHAR_NULL;
        }

    }
}
