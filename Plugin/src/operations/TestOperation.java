package operations;


import annotations.Operation;

@Operation
public class TestOperation {
    public static String echo(String input){
        return "Echo: " + input;
    }
}
