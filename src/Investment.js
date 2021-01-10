import React from 'react';
import { Link } from '@reach/router'

const Investment = ({riskManagementId, investment}) =>
    <tr>
     <td>{investment.wkn}</td>
     <td>{investment.name}</td>
     <td style={{ textAlign: 'right' }}>{investment.quantity}</td>
     <td style={{ textAlign: 'right' }}>{investment.purchasePrice}</td>
     <td style={{ textAlign: 'right' }}>{investment.investment}</td>
     <td style={{ textAlign: 'right' }}>{investment.notionalSalesPrice}</td>
     <td style={{ textAlign: 'right' }}>{investment.transactionCosts}</td>
     <td style={{ textAlign: 'right' }}>{investment.notionalRevenue}</td>
     <td style={{ textAlign: 'right' }}>{investment.positionRisk}</td>
     {
        investment._links && investment._links.sell ?
        <td><Link to={`/riskManagements/${riskManagementId}/sell-recommendations/${investment.id}`}>*</Link></td>
         : null
     }
    </tr>

export default Investment;