package modelsListeners;

import model.ClientListener;
import controller.command.CommandsNames;

public class ClientParser {
    private String msg;

    public String parseClient(ClientListener cl) {

        msg = getCommandWord(cl).toLowerCase();

        String qnhr;
        if ((qnhr = checkQNHRV(msg)) != null) {
            return qnhr;
        }

        switch (cl.getClientState()) {

            case QUIT: {
                return null;
            }

            case CONNECTION: {
                return CommandsNames.connecting;
            }
            case MAIL: {
                return parseMail(msg);
            }
            case COMMUNICATION: {
                return parseCommunication(msg);
            }

            case GET_MAIL_INFO: {
                if (msg.equals(CommandsNames.point)) {
                    return CommandsNames.point;
                }
            }

            case RCPT: {
                if (msg.equals(CommandsNames.data)) {
                    return CommandsNames.data;
                }

                if (msg.equals(CommandsNames.rcpt)) {
                    return CommandsNames.rcpt;
                }
            }
            default:
                return CommandsNames.unknown;
        }
    }

    private String checkQNHRV(String msg) {

        if (msg.equals(CommandsNames.vrfy)) {
            return CommandsNames.vrfy;
        }

        if (msg.equals(CommandsNames.quit)) {
            return CommandsNames.quit;
        }

        if (msg.equals(CommandsNames.noop)) {
            return CommandsNames.noop;
        }

        if (msg.equals(CommandsNames.rset)) {
            return CommandsNames.rset;
        }
        return null;
    }

    private String getCommandWord(ClientListener cl) {

        if (".".equals(cl.getLastMessage())) {
            return CommandsNames.point;
        }

        if (cl.getLastMessage().split(" ", 2) != null) {
            return cl.getLastMessage().split(" ", 2)[0];
        }
        return cl.getLastMessage().substring(1);
    }

    private String parseCommunication(String msg) {//нужно дописать комманды      

        if (msg.equals(CommandsNames.ehlo)) {
            return CommandsNames.ehlo;
        }
        if (msg.equals(CommandsNames.helo)) {
            return CommandsNames.helo;
        }
        if (msg.equals(CommandsNames.mail)) {
            return CommandsNames.mail;
        }

        if (msg.equals(CommandsNames.quit)) {
            return CommandsNames.quit;
        }
        return CommandsNames.unknown;
    }

    private String parseMail(String msg) {

        if (msg.equals(CommandsNames.rcpt)) {
            return CommandsNames.rcpt;
        }
        return CommandsNames.unknown;
    }
}