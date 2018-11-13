package socket;

import java.util.Arrays;

/**
 * Created by viones on 22/11/2017.
 */
public class Request {
    public RequestType type;
    public String[] params;

    public Request(RequestType type, String... params) {
        this.type = type;
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        for (int i = 0; i < params.length; i++)
            sb.append(" " + params[i]);
        return sb.toString();
    }

    public static Request parse(String line) {
        try {
            String[] items = line.trim().split("\\s+");

            switch (items[0].toUpperCase()) {
                case "SELL":
                    return new Request(RequestType.SELL, items[1], items[2]);
                case "BUY":
                    return new Request(RequestType.BUY, items[1], items[2]);
                case "VOTE":
                    return new Request(RequestType.VOTE, items[1], items[2]);
                case "ENDROUND":
                    return new Request(RequestType.ENDROUND);
                case "GETCASH":
                    return new Request(RequestType.GETCASH);
                case "GETSHARES":
                    return new Request(RequestType.GETSHARES);
                case "GETPRICES":
                    return new Request(RequestType.GETPRICES);
                case "GETCARDS":
                    return new Request(RequestType.GETCARDS);
                case "QUIT":
                case "LOGOUT":
                    return new Request(RequestType.LOGOUT);
                default:
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return new Request(RequestType.INVALID, line);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request other = (Request) obj;
        if (!Arrays.equals(params, other.params))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
