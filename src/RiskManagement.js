import React, { useState, useEffect } from "react";
import axios from "axios";

import Investment from './Investment'

const RiskManagement = () => {
  const [riskManagement, setRiskManagement] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
      const fetchRiskManagement = async () => {
          try {
            setLoading(true);
            setError('');

            const response = await axios("http://localhost:8080/api/risks/1")
            console.log("Received data: ", response.data);
            setRiskManagement(response.data);
         } catch (err) {
           setError(err);
         }
         setLoading(false)
     };

     fetchRiskManagement();
  }, []);

    if (loading) {
      return <p>loading..</p>;
    }

    if (error !== '') {
      return <p>ERROR: {error}</p>;
    }

return (
  <div>
    <table>
    <tbody>
    <tr><td><b>Gesamtkapital</b></td><td>{riskManagement.totalCapital}</td></tr>
    <tr><td><b>Einzelpositionrisiko</b></td><td>{riskManagement.individualPositionRiskInPercent}</td></tr>
    <tr><td></td><td>{riskManagement.individualPositionRisk}</td></tr>
    <tr>
    <td><b>WKN</b></td>
    <td><b>Name</b></td>
    <td><b>Stück</b></td>
    <td><b>Kaufkurs</b></td>
    <td><b>Einstiegssumme</b></td>
    <td><b>Fiktiver Verkaufspreis</b></td>
    <td><b>Kosten</b></td>
    <td><b>Fiktiver Verkaufserlös</b></td>
    <td><b>Risiko</b></td>
    </tr>
{
riskManagement.investments && riskManagement.investments.map((investment) => <Investment investment={investment}/>)
}
    <tr>
    <td><b>Depotwert</b></td>
    <td></td>
    <td></td>
    <td></td>
    <td>{riskManagement.totalInvestment}</td>
    <td></td>
    <td></td>
    <td>{riskManagement.totalRevenue}</td>
    <td>{riskManagement.depotRisk}</td>
    </tr>
    <tr><td><b>Depotrisiko</b></td><td>{riskManagement.depotRiskInPercent}</td></tr>
    <tr><td><b>Gesamtrisiko</b></td><td>{riskManagement.totalRiskInPercent}</td></tr>
    <td></td>
    </tbody>
    </table>
</div>)
};

export default RiskManagement