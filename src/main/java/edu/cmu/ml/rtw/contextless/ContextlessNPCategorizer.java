package edu.cmu.ml.rtw.contextless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.ml.rtw.contextless.np.NounPhrase;
import edu.cmu.ml.rtw.contextless.np.SimpeNPChuncks;
import edu.cmu.ml.rtw.contextless.np.WordSequence;
import edu.cmu.ml.rtw.generic.data.annotation.AnnotationType;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.PoSTag;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.TokenSpan;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP.Target;
import edu.cmu.ml.rtw.generic.model.annotator.nlp.AnnotatorTokenSpan;
import edu.cmu.ml.rtw.generic.util.Pair;
import edu.cmu.ml.rtw.generic.util.Triple;

public class ContextlessNPCategorizer implements AnnotatorTokenSpan<TypedNP> {

  public static final AnnotationTypeNLP<TypedNP> OUTOFCONTEXT_NP_CATEGORIES = new AnnotationTypeNLP<TypedNP>("nell-ooccat", TypedNP.class,
      Target.TOKEN_SPAN);

  static final AnnotationType<?>[] REQUIRED_ANNOTATIONS = new AnnotationType<?>[] {
      AnnotationTypeNLP.TOKEN,
      AnnotationTypeNLP.SENTENCE,
      AnnotationTypeNLP.POS };

  SimpeNPChuncks chunker;
  NELLTypes nellTypeClass;

  public ContextlessNPCategorizer() {
    try {
      chunker = new SimpeNPChuncks();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getName() {
    return "cmunell_ooccat-0.0.1";
  }

  public boolean measuresConfidence() {
    return false;
  }

  public AnnotationType<TypedNP> produces() {
    return OUTOFCONTEXT_NP_CATEGORIES;
  }

  public AnnotationType<?>[] requires() {
    return REQUIRED_ANNOTATIONS;
  }

  public List<Triple<TokenSpan, TypedNP, Double>> annotate(DocumentNLP document) {
    List<Triple<TokenSpan, TypedNP, Double>> annotationsNP = new ArrayList<Triple<TokenSpan, TypedNP, Double>>();

    if (nellTypeClass == null) {
      nellTypeClass = new NELLTypes();
    }
    HashMap<String, HashSet<String>> nellTypes = nellTypeClass.typedNPS;
    // add NER
    List<Pair<TokenSpan, String>> NER = document.getNer();
    for (Pair<TokenSpan, String> span : NER) {
      if (span.getSecond().toString().equals("O")) continue;

      String entity = span.getFirst().toString();
      if (nellTypes.get(entity) == null) {
        nellTypes.put(entity, new HashSet<String>());
      }
      nellTypes.get(entity).add(span.getSecond().toString().toLowerCase());

      entity = span.getFirst().toString().toLowerCase();
      if (nellTypes.get(entity) == null) {
        nellTypes.put(entity, new HashSet<String>());
      }
      nellTypes.get(entity).add(span.getSecond().toString().toLowerCase());

    }

    for (int sentenceIndex = 0; sentenceIndex < document.getSentenceCount(); sentenceIndex++) {
      List<PoSTag> tags = document.getSentencePoSTags(sentenceIndex);
      List<String> words = document.getSentenceTokenStrs(sentenceIndex);

      WordSequence wordSequence = new WordSequence();
      for (int j = 0; j < words.size(); j++) {
        wordSequence.appendTag(tags.get(j).name());
        wordSequence.appendWord(words.get(j));
      }

      List<NounPhrase> nps = chunker.locateNounPhrasePositionProperNouns(wordSequence);
      for (NounPhrase NP : nps) {
        String name = NP.toString().toLowerCase();
        if (nellTypes.get(name) != null) {
          TokenSpan tokenspan = new TokenSpan(document, sentenceIndex, NP.startInWordSequence(), NP.endInWordSequence()+1);

          for (String type : nellTypes.get(name)) {
            TypedNP tn = new TypedNP(NP.toString(), type);
            annotationsNP.add(new Triple<TokenSpan, TypedNP, Double>(tokenspan, tn, 0.8));
          }
        }
      }
    }

    return annotationsNP;
  }

}
