metaphor_app_dev
================
Overview

The web application is developed by Chao Yan for querying sentiment score, domains as well as other information of a metaphor sentence.
Users can query the metaphor sentences that have already been semantically indexed in the system, and they can also query any metaphor sentences they input to our system.

Technology specifications
The web application is build upon JAVA 7 platform and powered by Apache Tomcat 7. Our system follows the Model-View-Controller architecture and will be discussed in detail below.

--Model (Data Layer)
The module implements the JAVA beans behind the JAVA Servlet. The data are stored in files with different format. The metaphor corpus cleaned from LCC annotated corpus are stored as JSON list, each item with fields such as sentiment score, source, target and domain names. The metaphor sentences are indexed and managed by Apache Lucene and clustered semantically with random projection algorithm in "semanticvector" package. User can get most semantically similar metaphor sentences indexed in our system.

--View (User interface)
This part forms the front end of our web application. An AJAX page developed with Javascript can interact with back end through REST APIs

--Controller
JAVA Servlets which is implemented in Tomcat provide Controller functions to  deal with user request and interchange data with data layer. There are the following servlets:
   MetaphorServlet, return whole metaphor corpus or domain statistics or user-input metaphor query results.
   SimMetaphorServlet, return top N semantically related metaphor sentences for a query sentence.

Features
