import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] cmd = event.getMessage().getContentRaw().split(" ", 1);
        if(cmd[0].startsWith("!")) {
            switch (cmd[0].toLowerCase()) {
                case "!netflix":
                    if(Main.netflixAccounts.size() <= 0){
                        event.getChannel().sendMessage("We're sorry, but there are no more accounts left!").queue();
                    }

                    String account = Main.netflixAccounts.get((int)(Math.random() * Main.netflixAccounts.size()));
                    Main.netflixAccounts.remove(account);

                    MessageEmbed eb = new EmbedBuilder()
                            .setTitle("Bandit Accounts Bot")
                            .setDescription(String.format("Here is your account: `%s`", account)).build();
                    event.getChannel().sendMessage(eb).queue();
                    break;


            }
        }
    }
}