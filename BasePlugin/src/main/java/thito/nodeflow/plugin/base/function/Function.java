package thito.nodeflow.plugin.base.function;

public interface Function {
    boolean acceptState(FunctionState functionState);
    FunctionCanvas createCanvas(FunctionState functionState);
    FunctionState saveCanvas(FunctionCanvas canvas);
}
