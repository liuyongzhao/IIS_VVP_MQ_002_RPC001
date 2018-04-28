package iis.consumer.rpc.consumer.main;

import iis.consumer.rpc.consumer.RPC_C_DEMO;

public class RPC_C_DEMO_Main {
    public static void main(String[] args) throws Exception {
        RPC_C_DEMO rpc1 = new RPC_C_DEMO();
        System.out.println("请求返回2 对应的值 ");
        String response1 = rpc1.call("1");
        System.out.println(" 获取到的返回值是 '" + response1 + "'");
        rpc1.close();

        RPC_C_DEMO rpc2 = new RPC_C_DEMO();
        System.out.println("请求返回2 对应的值 ");
        String response2 = rpc2.call("2");
        System.out.println(" 获取到的返回值是 '" + response2 + "'");
        rpc2.close();

        RPC_C_DEMO rpc3 = new RPC_C_DEMO();
        System.out.println("请求返回2 对应的值 ");
        String response3 = rpc3.call("9");
        System.out.println(" 获取到的返回值是 '" + response3 + "'");
        rpc3.close();

    }
}
