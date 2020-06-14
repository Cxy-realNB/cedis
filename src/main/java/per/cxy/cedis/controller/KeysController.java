package per.cxy.cedis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cxy.cedis.model.DataTableResult;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.model.RedisObject;
import per.cxy.cedis.model.vo.DeleteKeysVO;
import per.cxy.cedis.model.vo.RenameKeyVO;
import per.cxy.cedis.model.vo.SaveKeyVO;
import per.cxy.cedis.service.KeysService;

import java.util.List;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/1 21:32
 */
@RestController
@RequestMapping("/v1/keys")
public class KeysController {

    private static final Logger logger = LoggerFactory.getLogger(KeysController.class);

    @Autowired
    private KeysService keysService;

    @GetMapping("/all/{name}")
    public List<Object> getAll(@PathVariable String name, int db) {
        if (name == null) return null;
        return keysService.getAll(name, db);
    }

    @GetMapping("/{name}")
    public List<Object> getAll(@PathVariable String name, int db, String match) {
        if (name == null || match == null) return null;
        return keysService.getAll(name, db, match);
    }

    @GetMapping("/page")
    public DataTableResult getAll(DataTableResult dataTableResult) {
        return keysService.getAll(dataTableResult);
    }

    @GetMapping("/value/{key}")
    public RedisObject getValue(@PathVariable String key, String name, int db) {
        if (key == null && name == null) return null;
        return keysService.getValue(db, key, name);
    }

    @PutMapping("/rename")
    public Message renameKey(@RequestBody RenameKeyVO renameKeyVO) {
        if (!renameKeyVO.noEmptyField()) return Message.getNullValueMessage();
        return keysService.renameKey(renameKeyVO);
    }

    @PutMapping("/")
    public Message updateRedisObject(@RequestBody SaveKeyVO saveKeyVO) {
        if (!saveKeyVO.noEmptyField()) return Message.getNullValueMessage();
        return keysService.updateRedisObject(saveKeyVO);
    }

    @PostMapping("/")
    public Message addKey(@RequestBody SaveKeyVO saveKeyVO) {
        if (!saveKeyVO.noEmptyField()) return Message.getNullValueMessage();
        return keysService.addKey(saveKeyVO);
    }

    @DeleteMapping("/")
    public Message deleteKeys(@RequestBody DeleteKeysVO deleteKeysVO) {
        if (!deleteKeysVO.noEmptyField()) return Message.getNullValueMessage();
        return keysService.deleteKeys(deleteKeysVO);
    }
}
