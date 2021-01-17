
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.main.TableMain;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class PropertyTableGenTest implements PropertyNotation, DirectoryLocation, MenuOptions {

    public static void main(String[] args) throws IOException, Exception {
        String dbo_ClassName = PropertyNotation.dbo_AAClass;
        String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
        String rawFiles = dbpediaDir + classDir + "rawFiles/";
        String outputDir = dbpediaDir + classDir + "object/";
        TableMain.generateClassPropertyTable(rawFiles, dbo_ClassName, outputDir);

    }

}
