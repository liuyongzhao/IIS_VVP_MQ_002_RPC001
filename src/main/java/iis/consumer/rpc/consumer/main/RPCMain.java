package iis.consumer.rpc.consumer.main;

import iis.consumer.rpc.consumer.RPCReceiver;

public class RPCMain {
    public static void main(String[] args) throws Exception {
        RPCReceiver rpcReceiver1 = new RPCReceiver();
        System.out.println("请求返回 1 对应的值 ");
        String response1 = rpcReceiver1.call("1");
        System.out.println(" 获取到的返回值是 '" + response1 + "'");

        RPCReceiver rpcReceiver2 = new RPCReceiver();
        System.out.println("请求返回 2 对应的值 ");
        String response2 = rpcReceiver2.call("2");
        System.out.println(" 获取到的返回值是 '" + response2 + "'");

        RPCReceiver rpcReceiver3 = new RPCReceiver();
        System.out.println("请求返回 3 对应的值 ");
        String response3 = rpcReceiver3.call("3");
        System.out.println(" 获取到的返回值是 '" + response3 + "'");

        RPCReceiver rpcReceiver4 = new RPCReceiver();
        System.out.println("请求返回 4 对应的值 ");
        String response4 = rpcReceiver4.call("4");
        System.out.println(" 获取到的返回值是 '" + response4 + "'");

        rpcReceiver1.close();
        rpcReceiver2.close();
        rpcReceiver3.close();
        rpcReceiver4.close();
    }
}
