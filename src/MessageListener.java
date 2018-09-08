import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        String[] cmd = event.getMessage().getContentRaw().split(" ", 2);
        if(cmd[0].startsWith("!")) {
            for(String s : Main.accountsList.keySet()){
                if(cmd[0].equalsIgnoreCase("!"+s)){
                    genAccount(event, s);
                    return;
                }
            }

            if(cmd[0].equalsIgnoreCase("!accountslist")){
                String possibleAccounts = "";
                for(String s : Main.accountsList.keySet()){
                    possibleAccounts += s + ", ";
                }
                event.getChannel().sendMessage("Possible accounts: " + possibleAccounts.substring(0, possibleAccounts.length()-2) + "\n\nType `!<account type> to generate an account.`").queue();
            } else if(cmd[0].equalsIgnoreCase("!commands") && event.getGuild().getId().equals("424023652044898304")){
                EmbedBuilder embed = new EmbedBuilder().setTitle("The Bandit Block Commands").setDescription("Please keep all commands in the #spam channel. If a command isn't working, alert an administrator. When executing a command, leave out the \"(   )\".").setColor(0x8000ff);
                embed.addField("!stockx (item)", "Checks the price of an item on StockX.", false);
                embed.addField("!funko (item)", "Checks the price of a Funko.", false);
                embed.addField("!proxies", "Displays the list of proxy providers and their discounts.", false);
                embed.addField("!accountslist", "Displays the list of accounts currently available to request.", false);
                embed.addField("!(account type)", "Requests an account.", false);
                embed.addField("!stock (account type)", "Checks the stock of a specific account type.", false);
                embed.addField("!bugreport (bug description)", "Submits a bug report to the developer.", false);
                embed.addField("!commands", "See this list.", true);
                event.getChannel().sendMessage(embed.build()).queue();
            } else if(cmd[0].equalsIgnoreCase("!bugreport")){
                EmbedBuilder eb = new EmbedBuilder().setTitle("Bug Report").setDescription(cmd[1]).setFooter("Submitted by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
                Main.jda.getUserById("139128331206393861").openPrivateChannel().complete().sendMessage(eb.build()).queue();
                event.getChannel().sendMessage("A bug report has been submitted.").queue();
            } else if (cmd[0].equalsIgnoreCase("!stock")) {
                new Thread(() -> {
                    if (cmd.length >= 2) {
                        for (String s : Main.accountsList.keySet()) {
                            if (cmd[1].equalsIgnoreCase(s)) {
                                event.getChannel().sendMessage("There are " + Main.accountsList.get(s).size() + " accounts in the " + s + " stock.").queue();
                                return;
                            }
                        }
                        event.getChannel().sendMessage("The account type " + cmd[1] + " does not exist.").queue();
                    } else if (isAdmin(event.getMember())){
                        String stock = "";
                        for(String s : Main.accountsList.keySet()){
                            stock += s + " - " + Main.accountsList.get(s).size() + "\n";
                        }
                        event.getChannel().sendMessage(stock).queue();
                    }
                }).start();
            }

            if(isAdmin(event.getMember())) {
                if (cmd[0].equalsIgnoreCase("!reload")) {
                    new Thread(() -> {
                        Main.loadAllAccounts();
                        event.getChannel().sendMessage("The accounts list has been refreshed.").complete();
                    }).start();
                }
            }
        }
    }

    private boolean isAdmin(Member m){
        return m.hasPermission(Permission.ADMINISTRATOR) || m.getUser().getId().equals("139128331206393861") || m.getUser().getId().equals("221070835165822978");
    }

    private void genAccount(MessageReceivedEvent event, String accountType){
        new Thread(() -> {
            try {
                PrivateChannel dm = openDM(event.getAuthor());

                Cooldown c = Cooldown.getCooldownForUser(event.getAuthor().getIdLong());
                if(c.getNumGenned() >= 1){
                    event.getChannel().sendMessage("You've generated too many accounts today! Try again in " + c.timeUntilReset() + ".").complete();
                    Main.log("User " + event.getAuthor().getName() + " tried to generate account of type " + accountType + " but they generated too many accounts today.");
                    return;
                }

                if (Main.accountsList.get(accountType).size() <= 0) {
                    event.getChannel().sendMessage("We're sorry, but there are no more " + accountType + " accounts at the moment! Please try again later.").complete();
                    Main.log("User " + event.getAuthor().getName() + " tried to generate account of type " + accountType + " but it was out of stock.");
                    return;
                }

                String account = Main.accountsList.get(accountType).get((int) (Math.random() * Main.accountsList.get(accountType).size()));

                MessageEmbed eb = new EmbedBuilder()
                        .setTitle("Accounts Bot")
                        .setDescription(String.format("Here is your account: `%s`", account)).build();
                dm.sendMessage(eb).complete();

                Main.accountsList.get(accountType).remove(account);
                Main.appendToFile("accounts/used/"+accountType+".txt", account+"\n");

                event.getChannel().sendMessage("An account was sent to your DMs!").queue();
                c.incNumGenned();

                Main.log("User " + event.getAuthor().getName() + " generated an account of type " + accountType);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().equals("50007: Cannot send messages to this user")) {
                    event.getChannel().sendMessage("Unable to send the account. Make sure you have DMs enabled!").queue();
                } else {
                    event.getChannel().sendMessage("An unknown error occurred while sending your account.").queue();
                }
            }
        }).start();
    }

    private PrivateChannel openDM(User u){
        try {
            PrivateChannel dm = u.openPrivateChannel().complete();
            return dm;
        } catch (Exception e){
            System.out.println("OOF " + e.getMessage());
        }
        return null;
    }
}