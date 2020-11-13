package com.wjbolles;

import com.wjbolles.eco.economy.BasicEconomyWrapper;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AdminMarketSpyFactory {

    public static final File pluginDirectory = new File("plugins/AdminMarket");

    @Mock
    private Server mockServer;
    @Mock
    private ItemFactory mockFactory;
    @Mock
    private ItemMeta mockedMeta;
    @Mock
    private CommandSender commandSender;
    @Mock
    BukkitScheduler mockScheduler;

    private AdminMarket plugin;

    // Server/Plugin components
    private PluginDescriptionFile pluginDescriptionFile;

    public AdminMarketSpyFactory(){
        MockitoAnnotations.initMocks(this);
        MockGateway.MOCK_STANDARD_METHODS = false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public AdminMarket getPlugin(){
        pluginDirectory.mkdirs();
        assertTrue(pluginDirectory.exists());

        generateItemMetaStubs();

        JavaPluginLoader mockPluginLoader = PowerMock.createMock(JavaPluginLoader.class);
        Whitebox.setInternalState(mockPluginLoader, "server", mockServer);

        Logger.getLogger("Minecraft").setParent(Logger.getLogger(AdminMarket.class.getName()));

        when(mockServer.getName()).thenReturn("TestBukkit");
        when(mockServer.getLogger()).thenReturn(Logger.getLogger(AdminMarket.class.getName()));

        generatePluginDescriptorFileStubs();

        plugin = PowerMockito.spy(new AdminMarket(mockPluginLoader,
                pluginDescriptionFile,
                pluginDirectory,
                new File(pluginDirectory,
                "testPluginFile")));

        try { PowerMockito.doNothing().when(plugin, "setupCommands"); } catch (Exception ignored) {}

        doReturn(pluginDirectory).when(plugin).getDataFolder();
        doReturn(true).when(plugin).isEnabled();
        doReturn(Logger.getLogger(AdminMarket.class.getName())).when(plugin).getLogger();

        generateCommandSchedulerStubs();
        generatePluginManagerStubs();

        Whitebox.setInternalState(plugin, "server", mockServer);
        
        generateCommandSenderStubs();

        stubAdminMarketMethods();

        return plugin;
    }

    private void generatePluginManagerStubs() {
        JavaPlugin[] plugins = new JavaPlugin[] { plugin };

        PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
        when(mockPluginManager.getPlugins()).thenReturn(plugins);
        when(mockPluginManager.getPlugin("AdminMarket")).thenReturn(plugin);
        when(mockPluginManager.getPermission(anyString())).thenReturn(null);
        when(mockServer.getPluginManager()).thenReturn(mockPluginManager);
    }

    private void generateCommandSchedulerStubs() {
        when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
                thenAnswer((Answer<Integer>) invocation -> {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                });
        when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
                thenAnswer((Answer<Integer>) invocation -> {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                });
        when(mockServer.getScheduler()).thenReturn(mockScheduler);
    }

    private void generateCommandSenderStubs() {
        // Init our command sender
        final Logger commandSenderLogger = Logger.getLogger("CommandSender");
        commandSenderLogger.setParent(Logger.getLogger(AdminMarket.class.getName()));
        doAnswer((Answer<Void>) invocation -> {
            commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
            return null;
        }).when(commandSender).sendMessage(anyString());
        when(commandSender.getServer()).thenReturn(mockServer);
        when(commandSender.getName()).thenReturn("MockCommandSender");
        when(commandSender.isPermissionSet(anyString())).thenReturn(true);
        when(commandSender.isPermissionSet(ArgumentMatchers.isA(Permission.class))).thenReturn(true);
        when(commandSender.hasPermission(anyString())).thenReturn(true);
        when(commandSender.hasPermission(ArgumentMatchers.isA(Permission.class))).thenReturn(true);
        when(commandSender.addAttachment(plugin)).thenReturn(null);
        when(commandSender.isOp()).thenReturn(true);
    }

    private void generatePluginDescriptorFileStubs() {

        pluginDescriptionFile = PowerMockito.spy(new PluginDescriptionFile("AdminMarket", "TEST",
                "com.wjbolles.AdminMarket"));
        when(pluginDescriptionFile.getAuthors()).thenReturn(new ArrayList<>());
    }

    private void stubAdminMarketMethods(){
        Logger logger = Logger.getAnonymousLogger();
        doReturn(logger).when(plugin).getLog();

        // Override the economy to not use Vault
        // Plugin Fields
        EconomyWrapper economy = new BasicEconomyWrapper(new HashMap<>());
        this.plugin.setEconomyWrapper(economy);

        plugin.onEnable();
    }

    private void generateItemMetaStubs(){
        /*
            ItemListing.equals() fails without this stub
            because the factory for getting an ItemMeta is only
            in the real (proprietary) server, which isn't available during testing.
        */
        doReturn(mockFactory).when(mockServer).getItemFactory();
        doReturn(mockedMeta).when(mockFactory).getItemMeta(any(Material.class));
    }

    public static void unregisterServer(){
        Whitebox.setInternalState(Bukkit.class, "server", (Server) null);
    }
}
