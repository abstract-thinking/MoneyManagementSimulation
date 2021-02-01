import React from 'react';
import { Link } from '@reach/router'

const Investment = ({riskManagementId, investment}) =>
    <tr>
     <td>{investment.wkn}</td>
     <td>{investment.name}</td>
     <td style={{ textAlign: 'right' }}>{investment.quantity}</td>
     <td style={{ textAlign: 'right' }}>{investment.purchasePrice.toFixed(2)}</td>
     <td style={{ textAlign: 'right' }}>{investment.investment.toFixed(2)}</td>
     <td style={{ textAlign: 'right' }}>{investment.notionalSalesPrice.toFixed(2)}</td>
     <td style={{ textAlign: 'right' }}>{investment.transactionCosts.toFixed(2)}</td>
     <td style={{ textAlign: 'right' }}>{investment.notionalRevenue.toFixed(2)}</td>
     <td style={{ textAlign: 'right' }}>{investment.positionRisk.toFixed(2)}</td>
     {
        investment._links && investment._links.sale ?
        <td><Link to={`/riskManagements/${riskManagementId}/sell-recommendations/${investment.id}`}>*</Link></td>
         : null
     }
    </tr>

export default Investment;