# LOaDing: WordFrameDisambiguation

LOaDing is a esource that enriches the Framester knowledge graph, which links Framenet, WordNet, VerbNet and other resources, with semantic features extracted from text corpora. 
Features are extracted from distributional semantics-based sense inventories and allow to connect the resource with text, for instance to boost the performance on Word Frame Disambiguation. 
Since Framester is a frame-based knowledge graph, which enables full-fledged OWL querying and reasoning, our resource paves the way for the development of novel, deeper semantic-aware applications 
that could benefit from the combination of knowledge from text and complex symbolic representations of events and participants.

We evaluated the resource in the task of WordFrameDisambiguation (WF)D and in this repository we share the source code and the datasets we produced for the evaluation.

Prerequirements<br>
In order to correctly execute the WFD:<br> 
<ol>
<li><b>Download/Checkout</b> the project;</li>
<li><b>Open</b> the project in NetBeans;</li>
<li><b>Compile</b> the project;</li>
</ol> 

Usage:<br>
Execute <i>wfd.sh PROFILENAME METHOD NORM</i><br> 
where:<br>
<ul>
   <li>PROFILENAME 
	<ul>
      <li>Framester: fn2bnBase, fn2bnDirectX, fn2bnFprofile, fn2bnFrameBase, fn2bnTransX, fn2bnXWFN</li>
      <li> LOaDing: ddt-wiki-n30-1400k-Base, ddt-wiki-n30-1400k-DirectX, ddt-wiki-n30-1400k-Fprofile, ddt-wiki-n30-1400k-Base, ddt-wiki-n30-1400k-TransX, ddt-wiki-n30-1400k-XWFN</li>
	</ul>
	</li>
 <li>METHOD:<ul>
      <li>oracle</li>
      <li>top-1</li>
	</ul>
	</li>
<li> NORM:
	<ul>
      <li>cond</li>
      <li>invnorm</li>
	</ul>
</li>
</ul>


