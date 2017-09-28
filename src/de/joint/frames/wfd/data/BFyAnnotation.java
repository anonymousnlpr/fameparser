/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.frames.wfd.data;

/**
 *
 * @author sfaralli
 */
public final class BFyAnnotation 
{
  private int tokenFramgment_start;  
  private int tokenFramgment_end;  
  
  private int charSegment_start;  
  private int charSegment_end;  
  private String babelSynsetID;
  private String DBpediaURL;
  // incapsulare
  // 
  private String BabelNetURL;
  private Double score;
  private Double coherenceScore;
  private Double globalScore;
  private String source;
  public BFyAnnotation(int tokenFramgment_start, 
                       int tokenFramgment_end, 
                       int charSegment_start, 
                       int charSegment_end,  
                       String babelSynsetID,
                       String DBpediaURL,
                       String BabelNetURL,
                       Double score,
                        Double coherenceScore,
                        Double globalScore,
                        String source)
    {
        setTokenFramgment_start(tokenFramgment_start);
        setTokenFramgment_end(tokenFramgment_end);
        setCharSegment_start(charSegment_start);
        setCharSegment_end(charSegment_end);
        setBabelSynsetID(babelSynsetID);
        setDBpediaURL(DBpediaURL);
        setBabelNetURL(BabelNetURL);
        setScore(score);
        setCoherenceScore(coherenceScore);
        setGlobalScore(globalScore);
        setSource(source);
   }
  
    /**
     * @return the tokenFramgment_start
     */
    public int getTokenFramgment_start() {
        return tokenFramgment_start;
    }

    /**
     * @param tokenFramgment_start the tokenFramgment_start to set
     */
    public void setTokenFramgment_start(int tokenFramgment_start) {
        this.tokenFramgment_start = tokenFramgment_start;
    }

    /**
     * @return the tokenFramgment_end
     */
    public int getTokenFramgment_end() {
        return tokenFramgment_end;
    }

    /**
     * @param tokenFramgment_end the tokenFramgment_end to set
     */
    public void setTokenFramgment_end(int tokenFramgment_end) {
        this.tokenFramgment_end = tokenFramgment_end;
    }

    /**
     * @return the charSegment_start
     */
    public int getCharSegment_start() {
        return charSegment_start;
    }

    /**
     * @param charSegment_start the charSegment_start to set
     */
    public void setCharSegment_start(int charSegment_start) {
        this.charSegment_start = charSegment_start;
    }

    /**
     * @return the charSegment_end
     */
    public int getCharSegment_end() {
        return charSegment_end;
    }

    /**
     * @param charSegment_end the charSegment_end to set
     */
    public void setCharSegment_end(int charSegment_end) {
        this.charSegment_end = charSegment_end;
    }

    /**
     * @return the babelSynsetID
     */
    public String getBabelSynsetID() {
        return babelSynsetID;
    }

    /**
     * @param babelSynsetID the babelSynsetID to set
     */
    public void setBabelSynsetID(String babelSynsetID) {
        this.babelSynsetID = babelSynsetID;
    }

    /**
     * @return the DBpediaURL
     */
    public String getDBpediaURL() {
        return DBpediaURL;
    }

    /**
     * @param DBpediaURL the DBpediaURL to set
     */
    public void setDBpediaURL(String DBpediaURL) {
        this.DBpediaURL = DBpediaURL;
    }

    /**
     * @return the BabelNetURL
     */
    public String getBabelNetURL() {
        return BabelNetURL;
    }

    /**
     * @param BabelNetURL the BabelNetURL to set
     */
    public void setBabelNetURL(String BabelNetURL) {
        this.BabelNetURL = BabelNetURL;
    }

    /**
     * @return the score
     */
    public Double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * @return the coherenceScore
     */
    public Double getCoherenceScore() {
        return coherenceScore;
    }

    /**
     * @param coherenceScore the coherenceScore to set
     */
    public void setCoherenceScore(Double coherenceScore) {
        this.coherenceScore = coherenceScore;
    }

    /**
     * @return the globalScore
     */
    public Double getGlobalScore() {
        return globalScore;
    }

    /**
     * @param globalScore the globalScore to set
     */
    public void setGlobalScore(Double globalScore) {
        this.globalScore = globalScore;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }
   
    @Override
    public String toString()
    {
     String result="";
      result=getTokenFramgment_start()+"\t"+getTokenFramgment_end()+"\t"+
            getCharSegment_start()+"\t"+getCharSegment_end()+"\t"+
             getBabelSynsetID()+"\t"+getDBpediaURL()+"\t"+ 
              getBabelNetURL()+"\t"+getScore()+"\t"+getCoherenceScore()+"\t"+
              +getGlobalScore()+"\t"+getSource();
      return result;
    }
}
