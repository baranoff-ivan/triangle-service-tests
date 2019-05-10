package triangle.service.tests.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTriangleRequest {

    private String separator;
    private String input;

    public CreateTriangleRequest(String input, String separator) {
        this.input = input;
        this.separator = separator;
    }

    public CreateTriangleRequest(String input) {
        this(input, null);
    }

    public String getInput() {
        return input;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public String toString() {
        return '{' +
                "input='" + input + "\', " +
                "separator='" + separator + '\'' +
                '}';
    }
}
