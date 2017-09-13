package com.wjbolles;

import com.wjbolles.command.actions.ItemListingActions;
import com.wjbolles.command.actions.QueryActions;
import com.wjbolles.command.actions.TransactionActions;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import com.wjbolles.eco.economy.BasicEconomyWrapperImpl;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.reflect.Whitebox;
import sun.rmi.runtime.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
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

    // Plugin Fields
    @Mock
    private Config config;
    private EconomyWrapper economy;
    private TransactionActions transactionActions;
    private QueryActions queryActions;
    private ItemListingActions itemListingActions;
    private Logger logger;
    private ItemListingDao listingDao;

    public AdminMarketSpyFactory(){
        MockitoAnnotations.initMocks(this);
        MockGateway.MOCK_STANDARD_METHODS = false;
    }

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

        doReturn(pluginDirectory).when(plugin).getDataFolder();
        doReturn(true).when(plugin).isEnabled();
        doReturn(Logger.getLogger(AdminMarket.class.getName())).when(plugin).getLogger();

        generateCommandSchedulerStubs();
        generatePluginManagerStubs();

        Whitebox.setInternalState(plugin, "server", mockServer);
        
        generateCommandSenderStubs();
        
        Bukkit.setServer(mockServer);

        stubAdminMarketMethods();
        plugin.createDirectory();
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
                thenAnswer(new Answer<Integer>() {
                    @Override
                    public Integer answer(InvocationOnMock invocation) throws Throwable {
                        Runnable arg;
                        try {
                            arg = (Runnable) invocation.getArguments()[1];
                        } catch (Exception e) {
                            return null;
                        }
                        arg.run();
                        return null;
                    }});
        when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
                thenAnswer(new Answer<Integer>() {
                    @Override
                    public Integer answer(InvocationOnMock invocation) throws Throwable {
                        Runnable arg;
                        try {
                            arg = (Runnable) invocation.getArguments()[1];
                        } catch (Exception e) {
                            return null;
                        }
                        arg.run();
                        return null;
                    }});
        when(mockServer.getScheduler()).thenReturn(mockScheduler);
    }

    private void generateCommandSenderStubs() {
        // Init our command sender
        final Logger commandSenderLogger = Logger.getLogger("CommandSender");
        commandSenderLogger.setParent(Logger.getLogger(AdminMarket.class.getName()));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                return null;
            }}).when(commandSender).sendMessage(anyString());
        when(commandSender.getServer()).thenReturn(mockServer);
        when(commandSender.getName()).thenReturn("MockCommandSender");
        when(commandSender.isPermissionSet(anyString())).thenReturn(true);
        when(commandSender.isPermissionSet(Matchers.isA(Permission.class))).thenReturn(true);
        when(commandSender.hasPermission(anyString())).thenReturn(true);
        when(commandSender.hasPermission(Matchers.isA(Permission.class))).thenReturn(true);
        when(commandSender.addAttachment(plugin)).thenReturn(null);
        when(commandSender.isOp()).thenReturn(true);
    }

    private void generatePluginDescriptorFileStubs() {
        pluginDescriptionFile = PowerMockito.spy(new PluginDescriptionFile("AdminMarket", "0.0.1",
                "com.wjbolles.AdminMarket"));
        when(pluginDescriptionFile.getAuthors()).thenReturn(new ArrayList<String>());
    }

    private void stubAdminMarketMethods(){
        this.logger = Logger.getAnonymousLogger();
        doReturn(logger).when(plugin).getLog();

        doReturn(config).when(plugin).getPluginConfig();

        this.economy = new BasicEconomyWrapperImpl(new HashMap<String, Double>());
        this.plugin.setEconomyWrapper(economy);

        this.listingDao = new ItemListingYamlDao(plugin);
        this.plugin.setListingDao(listingDao);

        this.queryActions = new QueryActions(plugin);
        this.plugin.setQueryActions(queryActions);

        this.transactionActions = new TransactionActions(plugin);
        this.plugin.setTransactionActions(transactionActions);

        this.itemListingActions = new ItemListingActions(plugin);
        this.plugin.setItemListingActions(itemListingActions);
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
