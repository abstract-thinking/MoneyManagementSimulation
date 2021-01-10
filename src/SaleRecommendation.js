import React, { useState, useEffect } from "react";
import axios from "axios";

const SaleRecommendation = () => {
  const [saleRecommendation, setSaleRecommendation] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
      const fetchSaleRecommendation = async () => {
          try {
            setLoading(true);
            setError('');

            const response = await axios("http://localhost:8080/api/risks/1/recommendations/sale/2")
            console.log("Received data: ", response.data);
            setSaleRecommendation(response.data);
         } catch (err) {
           setError(err);
         }
         setLoading(false)
     };

     fetchSaleRecommendation();
  }, []);

    if (loading) {
      return <p>loading..</p>;
    }

    if (error !== '') {
      return <p>ERROR: {error}</p>;
    }

return (
  <div>

    { saleRecommendation.shouldSellByFallingBelowTheLimit ? <h3>Current price {saleRecommendation.price} is lower then calculate position risk {sellRecommendation.initialNotionalSalesPrice}.</h3> : null }
    { saleRecommendation.shouldSellByRslComparison ? <h3>Company RSL {saleRecommendation.companyRsl} crosses exchange RSL {sellRecommendation.exchangeRsl}.</h3> : null }

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
     <td>{saleRecommendation.wkn}</td>
     <td>{saleRecommendation.company}</td>
     <td>{saleRecommendation.exchange}</td>
     <td style={{ textAlign: 'right' }}>{saleRecommendation.companyRsl}</td>
     <td style={{ textAlign: 'right' }}>{saleRecommendation.exchangeRsl}</td>
     <td style={{ textAlign: 'right' }}>{saleRecommendation.price}</td>
     <td style={{ textAlign: 'right' }}>{saleRecommendation.initialNotionalSalesPrice}</td>
    </tr>
    </tbody>
    </table>
  </div>
);
}

export default SaleRecommendation;