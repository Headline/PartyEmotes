package xyz.headlinedev.emotes;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;

@Value
@EqualsAndHashCode(callSuper = true)
public class PartyEmoteUpdate extends PartyMemberMessage
{
    private final int playerId;
    private final int emoteId;
}