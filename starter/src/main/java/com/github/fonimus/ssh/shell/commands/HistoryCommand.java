/*
 * Copyright (c) 2020 François Onimus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fonimus.ssh.shell.commands;

import com.github.fonimus.ssh.shell.SshShellHelper;
import com.github.fonimus.ssh.shell.SshShellProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.History;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Override history command to get history per user if not shared
 */
@Slf4j
@SshShellComponent
@ShellCommandGroup("Built-In Commands")
public class HistoryCommand implements History.Command {

    private SshShellProperties properties;

    private SshShellHelper helper;

    private org.jline.reader.History history;

    public HistoryCommand(SshShellProperties properties, SshShellHelper helper, org.jline.reader.History history) {
        this.properties = properties;
        this.helper = helper;
        this.history = history;
    }

    @ShellMethod(value = "Display or save the history of previously run commands")
    public List<String> history(@ShellOption(help = "A file to save history to.", defaultValue = ShellOption.NULL) File file) throws IOException {
        org.jline.reader.History historyToUse = this.history;
        if (!properties.isSharedHistory() && !helper.isLocalPrompt()) {
            LOGGER.debug("History is not shared and this is not from local prompt, getting specific user history");
            historyToUse = helper.getHistory();
        }
        return new History(historyToUse).history(file);
    }
}
