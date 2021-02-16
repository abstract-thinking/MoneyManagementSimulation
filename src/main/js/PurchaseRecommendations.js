import React, { useState, useEffect } from "react";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation"

const PurchaseRecommendations = (props) => {
  const [purchaseRecommendations, setPurchaseRecommendation] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/purchases`;

  useEffect(() => {
      const fetchPurchaseRecommendations = async () => {
          try {
            setLoading(true);
            setError('');

            const response = await axios(targetUrl)
            console.log("Received data: ", response.data);
            setPurchaseRecommendation(response.data);
         } catch (err) {
           setError(err);
         }
         setLoading(false)
     };

     console.log(targetUrl);
     fetchPurchaseRecommendations();
  }, [targetUrl]);

    if (loading) {
      return <p>loading..</p>;
    }

    if (error !== '') {
      return <p>ERROR: {error}</p>;
    }

return (
  <div className="container">
    <table>
    <thead>
    <tr>
     <th className="header">WKN</th>
     <th className="header">Name</th>
     <th className="header">Börse</th>
     <th className="header">RSL</th>
     <th className="header">Börse RSL</th>
     <th className="header">Anzahl</th>
     <th className="header">Preis</th>
     <th className="header">Fiktiver Verkaufspreis</th>
    </tr>
    </thead>
    <tbody>
        {
            purchaseRecommendations && purchaseRecommendations.map((purchaseRecommendation) =>
                <PurchaseRecommendation key={purchaseRecommendation.name} purchaseRecommendation={purchaseRecommendation} />
            )
        }
    </tbody>
    </table>
  </div>
);
}

export default PurchaseRecommendations;