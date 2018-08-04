import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
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
                event.getChannel().sendMessage("Possible accounts: " + possibleAccounts.substring(0, possibleAccounts.length()-2)).queue();
            }

            if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("139128331206393861")) {
                if (cmd[0].equalsIgnoreCase("!reload")) {
                    new Thread(() -> {
                        Main.loadAllAccounts();
                        event.getChannel().sendMessage("The accounts list has been refreshed.").complete();
                    }).start();
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
                        }
                    }).start();
                }
            }
        }
    }

    private void genAccount(MessageReceivedEvent event, String accountType){
        new Thread(() -> {
            try {
                PrivateChannel dm = openDM(event.getAuthor());

                Cooldown c = Cooldown.getCooldownForUser(event.getAuthor().getIdLong());
                if(c.getNumGenned() >= 5){
                    event.getChannel().sendMessage("You've generated too many accounts today! Try again in " + c.timeUntilReset() + ".").complete();
                    return;
                }

                if (Main.accountsList.get(accountType).size() <= 0) {
                    event.getChannel().sendMessage("We're sorry, but there are no more " + accountType + " accounts at the moment! Please try again later.").complete();
                    return;
                }

                String account = Main.accountsList.get(accountType).get((int) (Math.random() * Main.accountsList.get(accountType).size()));

                MessageEmbed eb = new EmbedBuilder()
                        .setTitle("Bandit Accounts Bot")
                        .setDescription(String.format("Here is your account: `%s`", account)).build();
                dm.sendMessage(eb).complete();

                Main.accountsList.get(accountType).remove(account);
                Main.appendToFile("accounts/used/"+accountType+".txt", account+"\n");

                event.getChannel().sendMessage("An account was sent to your DMs!").queue();
                c.incNumGenned();
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