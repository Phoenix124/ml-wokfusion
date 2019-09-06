package ml.ml.config;

import com.workfusion.vds.nlp.hypermodel.ie.generic.config.GenericIeHypermodelConfiguration;
import com.workfusion.vds.nlp.uima.resource.ClassPathResource;
import com.workfusion.vds.sdk.api.hypermodel.annotation.Import;
import com.workfusion.vds.sdk.api.hypermodel.annotation.ModelConfiguration;
import com.workfusion.vds.sdk.api.hypermodel.annotation.Named;
import com.workfusion.vds.sdk.api.nlp.annotator.Annotator;
import com.workfusion.vds.sdk.api.nlp.configuration.IeConfigurationContext;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;
import com.workfusion.vds.sdk.nlp.component.annotator.ner.AhoCorasickDictionaryNerAnnotator;
import com.workfusion.vds.sdk.nlp.component.annotator.ner.BaseRegexNerAnnotator;
import com.workfusion.vds.sdk.nlp.component.dictionary.CsvDictionaryKeywordProvider;
import ml.ml.fe.classification.IsInFirstOrLastNLinesInDocumentFeatureExtractor;
import ml.ml.fe.general.IsFitCustomPatternFeatureExtractor;
import ml.ml.fe.general.IsCoveredWithParticularNerFeatureExtractor;
import ml.ml.fe.general.IsFirstWordInDocumentFeatureExtractor;
import ml.ml.fe.general.IsPrecededWithCurrencySignFeatureExtractor;
import ml.ml.fe.general.IsPrecededWithKeywordsInLineFeatureExtractor;
import ml.ml.fe.general.IsUniqueWordInLineFeatureExtractor;
import ml.ml.fe.general.IsUpperLowerCaseFeatureExtractor;
import ml.ml.fe.table.IsLastCellInTableFeatureExtractor;
import ml.ml.fe.table.MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror;
import ml.ml.fe.table.IsCoveredWithCellFeatureExtractor;
import ml.ml.fe.table.TableColumnIndexFeatureExtractor;
import ml.ml.fe.table.TableRowIndexFeatureExtractor;
import ml.ml.processing.AmountNormalizationPostProcessor;
import ml.ml.processing.DatePostProcessing;
import ml.ml.processing.EmailPostProcessor;
import ml.ml.processing.ProductPostProcessor;
import ml.ml.processing.SupplierNamePostProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The model configuration class.
 * Here you can configure set of Feature Extractors, Annotators and Post-Processors.
 * Also you can import configuration with set of predefined components or your own configuration
 */
@ModelConfiguration
@Import(configurations = {
        @Import.Configuration(value = GenericIeHypermodelConfiguration.class)
})
public class QuickstartModelConfiguration {

    /**
     * Configure set of annotators.
     * DictionaryNerAnnotator - use some dictionary as an input, will annotate all words provided if they are found in text.
     * RegexNerAnnotator - annotate token ruled by some regular expression.
     *
     * @param context Execution context
     * @return List of specific annotators that will be applied for every document.
     */
    @Named("annotators")
    public List<Annotator<Document>> getAnnotators(IeConfigurationContext context) {
        List<Annotator<Document>> annotators = new ArrayList<>();

        //example of annotator based on dictionary
        AhoCorasickDictionaryNerAnnotator supplierNameNerAnnotator = new AhoCorasickDictionaryNerAnnotator(Fields.FIELD_NAME_SUPPLIER_NAME,
                new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/supplier_name.txt")));
        annotators.add(supplierNameNerAnnotator);
        //example of annotator based on regex
        BaseRegexNerAnnotator emailNerAnnotator = BaseRegexNerAnnotator.getJavaPatternRegexNerAnnotator(Fields.FIELD_NAME_EMAIL, Fields.EMAIL_REGEX);
        annotators.add(emailNerAnnotator);

        return annotators;
    }

    /**
     * Configure set of feature extractors with specific subset for every field, ruled by field code.
     *
     * @param context Execution context
     * @return List of specific feature extractors that will be applied for every field in documents.
     */
    @Named("featureExtractors")
    public List<FeatureExtractor<Element>> getFeatureExtractors(IeConfigurationContext context) {
        List<FeatureExtractor<Element>> featureExtractors = new ArrayList<>();

        String code = context.getField().getCode();
        switch (code) {
            case Fields.FIELD_NAME_SUPPLIER_NAME:
                Set<String> supplierNameKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/supplier_name_keywords.txt")).getDictionary();

                featureExtractors.add(new IsUniqueWordInLineFeatureExtractor<>());
                featureExtractors.add(new IsFirstWordInDocumentFeatureExtractor<>());
                featureExtractors.add(new IsUpperLowerCaseFeatureExtractor<>(true));
                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(5, true));
                featureExtractors.add(new IsCoveredWithParticularNerFeatureExtractor<>(Fields.FIELD_NAME_SUPPLIER_NAME));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_SUPPLIER_NAME, supplierNameKeywords));
                break;
            case Fields.FIELD_NAME_EMAIL:
//                Set<String> emailKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/email_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsCoveredWithParticularNerFeatureExtractor<>(Fields.FIELD_NAME_EMAIL));
//                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_SUPPLIER_NAME, emailKeywords));
                break;
            case Fields.FIELD_NAME_INVOICE_DATE:
//                Set<String> invoiceDateKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/invoice_date_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(5, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords, false));
//                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords));
                break;
            case Fields.FIELD_NAME_INVOICE_NUMBER:
//                Set<String> invoiceNumberKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/invoice_number_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(5, true));
//                featureExtractors.add(new IsUniqueWordInLineFeatureExtractor<>());
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords, false));
//                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords));
                break;
            case Fields.FIELD_NAME_PRICE:
//                Set<String> priceKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/price_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsCoveredWithCellFeatureExtractor<>());
//                featureExtractors.add(new IsPrecededWithCurrencySignFeatureExtractor<>());
//                featureExtractors.add(new TableColumnIndexFeatureExtractor<>());
//                featureExtractors.add(new TableRowIndexFeatureExtractor<>());
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_PRICE, priceKeywords, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_PRICE, priceKeywords, false));
                break;
            case Fields.FIELD_NAME_QUANTITY:
//                Set<String> quantityKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/quantity_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsCoveredWithCellFeatureExtractor<>());
//                featureExtractors.add(new TableColumnIndexFeatureExtractor<>());
//                featureExtractors.add(new TableRowIndexFeatureExtractor<>());
//                featureExtractors.add(new IsFitCustomPatternFeatureExtractor<>("is_number_quantity", "\\d+"));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_QUANTITY, quantityKeywords, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_QUANTITY, quantityKeywords, false));
                break;

            case Fields.FIELD_NAME_PRODUCT:
//                Set<String> productKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/product_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsCoveredWithCellFeatureExtractor<>());
//                featureExtractors.add(new TableColumnIndexFeatureExtractor<>());
//                featureExtractors.add(new TableRowIndexFeatureExtractor<>());
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_PRODUCT, productKeywords, true));
                break;
            case Fields.FIELD_NAME_TOTAL_AMOUNT:
//                Set<String> totalAmountKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/total_amount_keywords.txt")).getDictionary();
//
//                featureExtractors.add(new IsPrecededWithCurrencySignFeatureExtractor<>());
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords, true));
//                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords, false));
//                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords));
//                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(3, false));
//                featureExtractors.add(new IsLastCellInTableFeatureExtractor<>());
                break;
        }

        return featureExtractors;
    }

    /**
     * Configure set of post-processors for extracted data correction.
     *
     * @param context context
     * @return List of specific post-processors that will be applied for extracted values.
     */
    @Named("processors")
    public List<Processor<IeDocument>> getProcessors(IeConfigurationContext context) {
        return Arrays.asList(
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_PRICE),
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_TOTAL_AMOUNT),
                new SupplierNamePostProcessor(Fields.FIELD_NAME_SUPPLIER_NAME),
                new ProductPostProcessor(Fields.FIELD_NAME_PRODUCT),
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_QUANTITY),
                new DatePostProcessing(Fields.FIELD_NAME_INVOICE_DATE),
                new EmailPostProcessor(Fields.FIELD_NAME_EMAIL, Fields.EMAIL_REGEX));
    }
}