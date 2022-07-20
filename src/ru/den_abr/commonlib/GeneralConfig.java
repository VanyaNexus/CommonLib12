/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib;

import ru.den_abr.commonlib.configuration.Config;

public class GeneralConfig
        extends Config {
    private boolean replacePlaceholdersInChat = true;
    private boolean customIncomingChatHandler = true;
    private boolean replaceVaultEconomyService = true;
    private boolean debug = false;
    private boolean compatJson = false;
    private boolean printUnknownMessages = false;
    private boolean traceNametagConflicts = false;
    private boolean useNameForVaultQueries = false;
    private boolean savePlayerOnQuit = true;
    private int uuidLookupBatchSize = 25;
    private int menuMaxClickRate = 10;
    private long menuClickRatePeriod = 5000L;

    public int getMenuMaxClickRate() {
        return this.menuMaxClickRate;
    }

    public long getMenuClickRatePeriod() {
        return this.menuClickRatePeriod;
    }

    public boolean isReplacePlaceholdersInChat() {
        return this.replacePlaceholdersInChat;
    }

    public boolean isCustomIncomingChatHandler() {
        return this.customIncomingChatHandler;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getUuidLookupBatchSize() {
        return this.uuidLookupBatchSize;
    }

    public void setUuidLookupBatchSize(int uuidLookupBatchSize) {
        this.uuidLookupBatchSize = uuidLookupBatchSize;
    }

    public boolean useNameForVaultQueries() {
        return this.useNameForVaultQueries;
    }

    public boolean isCompatJson() {
        return this.compatJson;
    }

    public boolean isPrintUnknownMessages() {
        return this.printUnknownMessages;
    }

    public boolean isTraceNametagConflicts() {
        return this.traceNametagConflicts;
    }

    public boolean isReplaceVaultEconomyService() {
        return this.replaceVaultEconomyService;
    }

    public boolean isSavePlayerOnQuit() {
        return this.savePlayerOnQuit;
    }
}

