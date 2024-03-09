package etl.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SheetData {
    public List<Map<String, String>> master_dataList = new ArrayList<Map<String, String>>();
    public List<Map<String, String>> test_dataList = new ArrayList<Map<String, String>>();

    public List<String> master_headerList = new ArrayList<String>();
    public List<String> test_headerList = new ArrayList<String>();

    public void setMaster_dataList(Map<String, String> dict) {
        master_dataList.add(dict);
    }

    public void setTest_dataList(Map<String, String> dict) {
        test_dataList.add(dict);
    }

    public void setMaster_headerList(String header) {
        master_headerList.add(header);
    }

    public void setTest_headerList(String header) {
        test_headerList.add(header);
    }
}
