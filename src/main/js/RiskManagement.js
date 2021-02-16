import React, { useState, useEffect } from "react";
import axios from "axios";

import Investment from './Investment'

const RiskManagement = () => {
  const [riskManagement, setRiskManagement] = useState('');
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const riskManagementId = '1'

  useEffect(() => {
            setLoading(true)
            setError('')

            axios.get("http://localhost:8080/api/risks/" + riskManagementId)
                .then(response => {
                    setRiskManagement(response.data)
                    console.log("Received data: ", response.data)
                    setLoading(false)
                })
                .catch (error =>  {
                    console.log("Error: ", error)

                    setLoading(false)
                    setError("Error: " + error)
                })
     }, [])

    if (isLoading) {
      return <p>loading..</p>;
    }

    if (error !== '') {
      return <p>ERROR: {error}</p>;
    }

    if (riskManagement === '') {
        return <p>loading starts now</p>
    }

return (
  <div>
    <table>
    <tbody>
    <tr><td><b>Gesamtkapital</b></td><td>{riskManagement.totalCapital.toFixed(2)}</td></tr>
    <tr><td><b>Einzelpositionrisiko</b></td><td>{riskManagement.individualPositionRiskInPercent.toFixed(2)}</td></tr>
    <tr><td></td><td>{riskManagement.individualPositionRisk.toFixed(2)}</td></tr>
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
        riskManagement.investments && riskManagement.investments.map((investment) =>
        <Investment key={investment.name} riskManagementId={riskManagementId} investment={investment}/>)
    }
    <tr>
        <td><b>Depotwert</b></td>
        <td colSpan={3}></td>
        <td style={{ textAlign: 'right' }}>{riskManagement.totalInvestment.toFixed(2)}</td>
        <td colSpan={2}></td>
        <td style={{ textAlign: 'right' }}>{riskManagement.totalRevenue.toFixed(2)}</td>
        <td style={{ textAlign: 'right' }}>{riskManagement.depotRisk.toFixed(2)}</td>
    </tr>
    <tr>
        <td><b>Depotrisiko</b></td>
        <td colSpan={7}></td>
        <td style={{ textAlign: 'right' }}>{riskManagement.depotRiskInPercent.toFixed(2)}</td>
    </tr>
    <tr>
        <td><b>Gesamtrisiko</b></td><td>{riskManagement.totalRiskInPercent.toFixed(2)}</td>
    </tr>
    </tbody>
    </table>
  </div>
)
};

export default RiskManagement