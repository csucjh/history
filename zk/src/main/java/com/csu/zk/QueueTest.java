package com.csu.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.nio.ByteBuffer;
import java.util.List;


/**
 * Producer-Consumer queue
 */
public class QueueTest extends SyncPrimitive {

    /**
     * Constructor of producer-consumer queue
     *
     * @param address
     * @param name
     */
    QueueTest(String address, String name) {
        super(address, name);
    }

    /**
     * Add element to the queue.
     *
     * @param i
     * @return
     */

    boolean produce(int i) throws KeeperException, InterruptedException {
        ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;

        // Add child with value i
        b.putInt(i);
        value = b.array();
        zk.create(root + "/element", value, Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);

        return true;
    }

    /**
     * Remove first element from the queue.
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    int consume() throws KeeperException, InterruptedException {
        int retvalue = -1;
        Stat stat = null;

        // Get the first element available
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() == 0) {
                    System.out.println("Going to wait");
                    mutex.wait();
                } else {
                    Integer min = new Integer(list.get(0).substring(7));
                    String minNode = list.get(0);
                    for (String s : list) {
                        Integer tempValue = new Integer(s.substring(7));
                        //System.out.println("Temporary value: " + tempValue);
                        if (tempValue < min) {
                            min = tempValue;
                            minNode = s;
                        }
                    }
                    System.out.println("Temporary value: " + root + "/" + minNode);
                    byte[] b = zk.getData(root + "/" + minNode,
                            false, stat);
                    zk.delete(root + "/" + minNode, 0);
                    ByteBuffer buffer = ByteBuffer.wrap(b);
                    retvalue = buffer.getInt();

                    return retvalue;
                }
            }
        }
    }

    /**
     * java QueueTest localhost 100 p
     * java QueueTest localhost 100 c
     *
     * @param args
     */
    public static void main(String args[]) {
        QueueTest q = new QueueTest(args[0], "/app1");

        System.out.println("Input: " + args[0]);
        int i;
        Integer max = new Integer(args[1]);

        if (args[2].equals("p")) {
            System.out.println("Producer");
            for (i = 0; i < max; i++)
                try {
                    q.produce(10 + i);
                } catch (KeeperException e) {

                } catch (InterruptedException e) {

                }
        } else {
            System.out.println("Consumer");

            for (i = 0; i < max; i++) {
                try {
                    int r = q.consume();
                    System.out.println("Item: " + r);
                } catch (KeeperException e) {
                    i--;
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
