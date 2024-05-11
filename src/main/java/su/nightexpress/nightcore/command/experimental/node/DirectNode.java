package su.nightexpress.nightcore.command.experimental.node;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.flag.CommandFlag;
import su.nightexpress.nightcore.command.experimental.flag.ContentFlag;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Placeholders;

import java.util.*;

public class DirectNode extends CommandNode implements DirectExecutor {

    private final List<CommandArgument<?>> arguments;
    private final Map<String, CommandFlag> flags;
    private final DirectExecutor executor;

    private final int requiredArguments;

    public DirectNode(@NotNull final NightCorePlugin plugin, @NotNull final String name, @NotNull final String[] aliases, @NotNull final String description,
            @Nullable final String permission, final boolean playerOnly, @NotNull final List<CommandArgument<?>> arguments,
            @NotNull final Map<String, CommandFlag> flags, @NotNull final DirectExecutor executor) {
        super(plugin, name, aliases, description, permission, playerOnly);
        this.arguments = Collections.unmodifiableList(arguments);
        this.flags = Collections.unmodifiableMap(flags);
        this.executor = executor;
        this.requiredArguments = (int) this.arguments.stream().filter(CommandArgument::isRequired).count();
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull final NightCorePlugin plugin, @NotNull final String... aliases) {
        return new DirectNodeBuilder(plugin, aliases);
    }

    @Override
    public boolean execute(@NotNull final CommandContext context, @NotNull final ParsedArguments arguments) {
        return this.executor.execute(context, arguments);
    }

    @Override
    protected boolean onRun(@NotNull final CommandContext context) {
        final CommandSender sender = context.getSender();
        final String[] args = context.getArgs();
        int index = context.getArgumentIndex();
        // System.out.println("plaincmd args = " + Arrays.toString(args));
        // System.out.println("plaincmd index = " + index);

        final ParsedArguments parsedArguments = new ParsedArguments();
        for (final CommandArgument<?> argument : this.arguments) {
            if (index >= args.length)
                break;

            if (!argument.hasPermission(sender)) {
                context.errorPermission();
                return false;
            }

            final String arg = args[index++];
            final ParsedArgument<?> parsedArgument = argument.parse(arg);
            if (parsedArgument == null) {
                return context.sendFailure(argument.getFailureMessage().replace(Placeholders.GENERIC_VALUE, arg)
                        .replace(Placeholders.GENERIC_NAME, argument.getLocalized()));
            }

            parsedArguments.add(argument, parsedArgument);
        }

        if (parsedArguments.getArgumentMap().size() < this.requiredArguments) {
            return context.sendFailure(CoreLang.ERROR_COMMAND_USAGE.getMessage(this.plugin)
                    .replace(Placeholders.COMMAND_LABEL, this.getNameWithParents()).replace(Placeholders.COMMAND_USAGE, this.getUsage()));
        }

        if (!this.flags.isEmpty() && index < args.length) {
            for (int flagIndex = index; flagIndex < args.length; flagIndex++) {
                final String arg = args[flagIndex];
                if (arg.charAt(0) != CommandFlag.PREFIX)
                    continue;

                final int delimiterIndex = arg.indexOf(ContentFlag.DELIMITER);
                final boolean hasDelimiter = delimiterIndex != -1;

                final String flagName = (hasDelimiter ? arg.substring(0, delimiterIndex) : arg).substring(1);
                final CommandFlag flag = this.getFlag(flagName);
                if (flag == null || parsedArguments.hasFlag(flag) || !flag.hasPermission(sender))
                    continue;

                if (flag instanceof final ContentFlag<?> contentFlag) {
                    if (!hasDelimiter)
                        continue;

                    final String content = arg.substring(delimiterIndex + 1);
                    if (content.isEmpty())
                        continue;

                    final ParsedArgument<?> parsed = contentFlag.parse(content);
                    if (parsed == null) {
                        context.send(CoreLang.ERROR_COMMAND_PARSE_FLAG.getMessage().replace(Placeholders.GENERIC_VALUE, content)
                                .replace(Placeholders.GENERIC_NAME, flag.getName()));
                        continue;
                    }

                    parsedArguments.addFlag(flag, parsed);
                } else {
                    parsedArguments.addFlag(flag, new ParsedArgument<>(true));
                }
            }
        }

        return this.execute(context, parsedArguments);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull final TabContext context) {
        final int index = context.getArgs().length - (context.getIndex() + 1);
        // System.out.println("index = " + index);
        // System.out.println("arguments.size() = " + arguments.size());
        if (index >= this.arguments.size()) {
            final List<String> samples = new ArrayList<>();

            this.getFlags().forEach(commandFlag -> {
                if (!commandFlag.hasPermission(context.getSender()))
                    return;
                if (commandFlag instanceof final ContentFlag<?> contentFlag) {
                    samples.add(contentFlag.getSampled());
                } else {
                    samples.add(commandFlag.getPrefixed());
                }
            });

            return samples;
        }

        final CommandArgument<?> argument = this.arguments.get(index);
        if (!argument.hasPermission(context.getSender()))
            return Collections.emptyList();

        // System.out.println("argument = " + argument);
        return argument.getSamples(context);
    }

    @Override
    @NotNull
    public String getUsage() {
        final StringBuilder labelBuilder = new StringBuilder();

        this.arguments.forEach(argument -> {
            if (!labelBuilder.isEmpty()) {
                labelBuilder.append(" ");
            }
            labelBuilder.append(argument.getLocalized());
        });

        final StringBuilder flagBuilder = new StringBuilder();
        this.flags.values().forEach(commandFlag -> {
            if (!flagBuilder.isEmpty()) {
                flagBuilder.append(" ");
            }
            flagBuilder.append(commandFlag.getPrefixedFormatted());
        });

        if (!flagBuilder.isEmpty()) {
            labelBuilder.append(" ").append(flagBuilder);
        }

        return labelBuilder.toString();
    }

    @NotNull
    public List<CommandArgument<?>> getArguments() { return this.arguments; }

    @Nullable
    public CommandFlag getFlag(@NotNull final String name) { return this.flags.get(name.toLowerCase()); }

    @NotNull
    public Collection<CommandFlag> getFlags() { return this.flags.values(); }
}
