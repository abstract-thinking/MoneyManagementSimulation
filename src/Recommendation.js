import React, { useState, useEffect } from "react";
import axios from "axios";

const Recommendation = () => {
  const [sellRecommendation, setSellRecommendation] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
      const fetchSellRecommendation = async () => {
          try {
            setLoading(true);
            setError('');

            const response = await axios("http://localhost:8080/api/risks/1/sell-recommendations/2")
            console.log("Received data: ", response.data);
            setSellRecommendation(response.data);
         } catch (err) {
           setError(err);
         }
         setLoading(false)
     };

     fetchSellRecommendation();
  }, []);

    if (loading) {
      return <p>loading..</p>;
    }

    if (error !== '') {
      return <p>ERROR: {error}</p>;
    }

return (
  <div>

    { sellRecommendation.shouldSellByFallingBelowTheLimit ? <h3>Current price {sellRecommendation.price} is lower then calculate position risk {sellRecommendation.initialNotionalSalesPrice}.</h3> : null }
    { sellRecommendation.shouldSellByRslComparison ? <h3>Company RSL {sellRecommendation.companyRsl} crosses exchange RSL {sellRecommendation.exchangeRsl}.</h3> : null }

    <table>
    <thead>
    <tr>
     <th>WKN</th>
     <th>Name</th>
     <th>Börse</th>
     <th>RSL</th>
     <th>Börse RSL</th>
     <th>Preis</th>
     <th>Verkaufspreisschwelle</th>
    </tr>
    </thead>
    <tbody>
    <tr>
     <td>{sellRecommendation.wkn}</td>
     <td>{sellRecommendation.company}</td>
     <td>{sellRecommendation.exchange}</td>
     <td style={{ textAlign: 'right' }}>{sellRecommendation.companyRsl}</td>
     <td style={{ textAlign: 'right' }}>{sellRecommendation.exchangeRsl}</td>
     <td style={{ textAlign: 'right' }}>{sellRecommendation.price}</td>
     <td style={{ textAlign: 'right' }}>{sellRecommendation.initialNotionalSalesPrice}</td>
    </tr>
    </tbody>
    </table>
  </div>
);
}

export default SellRecommendation;