package ml.ml.model;

import com.workfusion.automl.hypermodel.ie.generic.IeGenericSe20Hypermodel;
import com.workfusion.vds.sdk.api.hypermodel.ModelType;
import com.workfusion.vds.sdk.api.hypermodel.annotation.HypermodelConfiguration;
import com.workfusion.vds.sdk.api.hypermodel.annotation.ModelDescription;

import ml.ml.config.QuickstartModelConfiguration;

/**
 * The model class. Define here your model details like code, version etc.
 */
@ModelDescription(
        code = "ml.ml",
        title = "ml.ml",
        description = "ml.ml",
        version = "0.0.1-SNAPSHOT",
        type = ModelType.IE
)
@HypermodelConfiguration(QuickstartModelConfiguration.class)
public class QuickstartModel extends IeGenericSe20Hypermodel {}