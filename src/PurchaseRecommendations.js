import React, { useState, useEffect } from "react";
import axios from "axios";

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
  <div>
    <table>
    <thead>
    <tr>
     <th>WKN</th>
     <th>Name</th>
     <th>Börse</th>
     <th>RSL</th>
     <th>Börse RSL</th>
     <th>Anzahl</th>
     <th>Preis</th>
     <th>Fiktiver Verkaufspreis</th>
    </tr>
    </thead>
    <tbody>
        {
            purchaseRecommendations && purchaseRecommendations.map((purchaseRecommendation) =>
                <tr>
                 <td>{purchaseRecommendation.wkn}</td>
                 <td>{purchaseRecommendation.name}</td>
                 <td>{purchaseRecommendation.exchange}</td>
                 <td>{purchaseRecommendation.rsl}</td>
                 <td>{purchaseRecommendation.exchangeRsl}</td>
                 <td>{purchaseRecommendation.quantity}</td>
                 <td>{purchaseRecommendation.price}</td>
                 <td>{purchaseRecommendation.notionalSalesPrice}</td>
                </tr>
                )
        }
    </tbody>
    </table>
  </div>
);
}

export default PurchaseRecommendations;