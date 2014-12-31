package org.elasticsearch.river.jdonrefv4.test.jdonrefindextest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.FrequentTermsUtil;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.InitParameters;
import org.junit.Test;

public class ValideN {

    @Test
    public void valideTestsAfterIndexation() {
        FrequentTermsUtil.setFilePath("./src/resources/analysis/word84.txt");
        InitParameters initParam = InitParameters.getInstance();
        initParam.allDeptInit();
        String[] departementsIDF = {"95", "94", "93", "92", "91", "78", "77", "75"};
        initParam.init2(departementsIDF);
        try {
            initParam.getJDONREFIndexV2();

        } catch (SQLException | IOException ex) {
            Logger.getLogger(ValideN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}