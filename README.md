<h1> File Similarity Application 🗃️</h1>

<h2> Overall Description 💬 </h3>
<p> It is a Spring Boot Application backed by a RESTful APIs to compute the similarity score between a reference file (file A) and different files within a directory pool. This similarity is being conducted through 2 methods (cosine similarity, and traditional method)</p>

<h2> File Similarity Methods 🧾 </h2>

<li><h3>Cosine Similarity</h3> Similar to the word embedding method in the domain of Large Language Models (LLMs), which is reminiscent of the available chatbot agents nowadays. In this method we start by following this procedure:
<ol>
  <li> Create a frequency map of the ground of truth file ( file A ). This frequency map will look like something like that {"hello":12} where the word is "hello" and it occurred 12 times in the file</li>
  <li> Create a vector representation of the file A's frequency map.</li>
  <li> Repeat steps 1 and 2 for every other file </li>
  <li> Get the dot product of the keys of both frequency maps and calculate the cosine similarity index</li>
</ol></li>
  <li><h3>Traditional Method</h3> This method is more straightforward since we are calculating the number of matching words against the total number of words in file A, thus we can get an accurate similarity percentage yet through a simpler means.</li>

<h2>Important Insights 💡 </h2>
 <ol>
  <li> To manage the large sizes of the participating files, a parallel thread approach was followed in order to create multiple working threads to read the files simultaneously thus reducing the execution time. </li>
  <li> The code is split into <code>services</code> and <code>Controllers</code>, where the detailed implementation of the traditional file similarity method and cosine similarity are under <code>services</code> </li>
   <li> in order to test the program, just download it and run it, then call the api <code>/compareCosine</code> for the cosine similarity and the <code>/compareNormal</code> for the traditional method </li>
 </ol>


