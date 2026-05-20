package com.phuc.ftpclient.threads;

import com.phuc.ftpclient.Client;

public class SendCmdGUIThread extends SendCommandThread {

    public SendCmdGUIThread(Client client) {
        super(client);
    }

    @Override
    protected String getCommandFromUser() {
        return "";
    }

}
