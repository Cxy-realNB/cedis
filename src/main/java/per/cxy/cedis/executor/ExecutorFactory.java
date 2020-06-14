package per.cxy.cedis.executor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.cxy.cedis.executor.api.BaseExecutable;
import per.cxy.cedis.model.ClientParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:45
 */
public class ExecutorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorFactory.class);

    public static BaseExecutable createExecutor(ClientParams clientParams) {
        if (ExecutorContainer.containsKey(clientParams.getName())) return null;
        SingleExecutor singleExecutor = new SingleExecutor(clientParams);
        if (singleExecutor.isCluster()) {
            logger.info("This is a cluster node.");
            ClusterExecutor clusterExecutor = new ClusterExecutor(clientParams);
            ExecutorContainer.put(clusterExecutor);
            return clusterExecutor;
        }else {
            logger.info("This is a single node.");
            ExecutorContainer.put(singleExecutor);
        }
        return singleExecutor;
    }

    public static BaseExecutable createExecutor(ClientParams clientParams, boolean open) {
        if (open) {
            return createExecutor(clientParams);
        }else {
            if (ExecutorContainer.containsKey(clientParams.getName())) return null;
            SingleExecutor singleExecutor = new SingleExecutor(clientParams, false);
            ExecutorContainer.put(singleExecutor);
            return singleExecutor;
        }
    }

    public static BaseExecutable singleNodeToCluster(ClientParams clientParams) {
        ClusterExecutor clusterExecutor = new ClusterExecutor(clientParams);
        ExecutorContainer.put(clusterExecutor);
        return clusterExecutor;
    }

    public static void initialConnections(String filePath) {
        if (StringUtils.isBlank(filePath)) return;
        File file = new File(filePath);
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        if (file.isFile() && file.exists()) {
            byte[] temp = new byte[1024];
            int read = 0;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    if (!((read = in.read(temp)) != -1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String str = new String(temp, 0, read);
                sb.append(str);
            }
            JSONArray jsonArray = JSONArray.fromObject(sb.toString());
            Object[] clientParams = jsonArray.toArray();
            for (Object c : clientParams) {
                ClientParams cp = new ClientParams((JSONObject) c);
                cp.toRedisUri();
                logger.info("{} is creating! Host: {}, Port:{}", cp.getName(), cp.getHost(), cp.getPort());
                createExecutor(cp, false);
            }
        } else {
            logger.error("File {} not found!", filePath);
        }
    }

    public static String persistConnection(String filePath) {
        List<ClientParams> clientParams = ExecutorContainer.getAll().stream().map(t -> t.getDisplayParams().getClientParams()).collect(Collectors.toList());
        JSONArray jsonArray = new JSONArray();
        clientParams.forEach(t -> jsonArray.add(t.toJson()));
        String json = jsonArray.toString();
        File file = new File(filePath);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(json);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Success!";
    }
}
