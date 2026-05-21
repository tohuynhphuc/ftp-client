package com.phuc.ftpclient.threads;

import com.phuc.ftpclient.Client;

public class SendCmdGUIThread extends SendCommandThread {

    @Override
    protected String getCommandFromUser() {
        return "";
    }

}
