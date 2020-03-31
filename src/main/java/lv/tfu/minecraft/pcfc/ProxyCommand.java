package lv.tfu.minecraft.pcfc;

public class ProxyCommand {
    private final String alias;
    private final double cost;
    private final boolean giveOp;
    private final String command;

    public ProxyCommand(String alias, double cost, boolean giveOp, String command) {
        this.alias = alias;
        this.cost = cost;
        this.giveOp = giveOp;
        this.command = command;
    }

    public String getAlias() {
        return alias;
    }

    public double getCost() {
        return cost;
    }

    public String getCostToString() {
        return String.valueOf(cost);
    }

    public boolean isGiveOp() {
        return giveOp;
    }

    public String getCommand() {
        return command;
    }

    public String getServerCommand() {
        return command.replaceAll("^/", "");
    }
}
