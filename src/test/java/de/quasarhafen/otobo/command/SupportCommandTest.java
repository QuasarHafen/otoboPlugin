package de.quasarhafen.otobo.command;

import de.quasarhafen.otobo.OtoboPlugin;
import de.quasarhafen.otobo.OtoboService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportCommandTest {

    @Mock
    private OtoboPlugin plugin;

    @Mock
    private OtoboService service;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private CommandSender sender;

    private SupportCommand supportCommand;

    @BeforeEach
    void setUp() {
        supportCommand = new SupportCommand(plugin);
    }

    @Test
    void onCommandReturnsTrueForNonPlayerSender() {
        boolean result = supportCommand.onCommand(sender, command, "support", new String[]{"hello"});

        assertTrue(result);
        verifyNoInteractions(service);
    }

    @Test
    void onCommandWithoutArgsShowsUsageMessage() {
        boolean result = supportCommand.onCommand(player, command, "support", new String[]{});

        assertTrue(result);
        verify(player).sendMessage(contains("support"));
        verifyNoInteractions(service);
    }

    @Test
    void onCommandWithArgsCreatesTicket() {
        when(plugin.getService()).thenReturn(service);

        boolean result = supportCommand.onCommand(player, command, "support", new String[]{"Need", "help"});

        assertTrue(result);
        verify(player).sendMessage(contains("Ticket wird erstellt"));
        verify(service).createTicket(eq(player), eq("Need help"));
    }

    @Test
    void onTabCompleteReturnsEmptyList() {
        List<String> completions = supportCommand.onTabComplete(sender, command, "support", new String[]{"a"});

        assertTrue(completions.isEmpty());
        assertEquals(0, completions.size());
    }
}
